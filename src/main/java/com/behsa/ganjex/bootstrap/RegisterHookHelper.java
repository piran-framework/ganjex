package com.behsa.ganjex.bootstrap;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import com.behsa.ganjex.api.StartupHook;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * bootstrapping helper class to register all libraries hooks into lifecycleManagement
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
class RegisterHookHelper {
	private static final Logger log = LoggerFactory.getLogger(RegisterHookHelper.class);

	private static Map<Class<?>, Object> objectsWithHooks = new HashMap<>();


	static void loadHooks() {
		Reflections.log = LoggerFactory.getLogger(Reflections.class);
		Reflections libraries = new Reflections("com.behsa", new MethodAnnotationsScanner(),
						Bootstrap.libClassLoader());
		Set<Method> startupHookMethods = libraries.getMethodsAnnotatedWith(StartupHook.class);
		Set<Method> shutdownHooksMethods = libraries.getMethodsAnnotatedWith(ShutdownHook.class);
		startupHookMethods.forEach(RegisterHookHelper::addDeclaringClass);
		shutdownHooksMethods.forEach(RegisterHookHelper::addDeclaringClass);
		startupHookMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
						.forEach(method -> Bootstrap.lifecycleManagement().registerStartupHook(
										new com.behsa.ganjex.lifecycle.StartupHook(createHook(method))));
		shutdownHooksMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
						.forEach(method -> Bootstrap.lifecycleManagement().registerShutdownHook(
										new com.behsa.ganjex.lifecycle.ShutdownHook(createHook(method))));
	}

	private static void addDeclaringClass(Method m) {
		if (!objectsWithHooks.keySet().contains(m.getDeclaringClass())) {
			Constructor<?> constructor;
			try {
				constructor = m.getDeclaringClass().getConstructor((Class<?>[]) null);
			} catch (NoSuchMethodException e) {
				log.error("could not find default constructor for class {} ignoring the {} hook",
								m.getDeclaringClass().getName(), m.getName());
				return;
			}
			try {
				Object hookClassObj = constructor.newInstance((Object[]) null);
				objectsWithHooks.put(m.getDeclaringClass(), hookClassObj);
			} catch (InstantiationException e) {
				log.error("class {} should not be abstract, ignoring the {} hook",
								m.getDeclaringClass().getName(), m.getName(), e);
			} catch (IllegalAccessException e) {
				log.error("class {} constructor is not accessible for the container, default constructor " +
												"should be public, ignoring the {} hook",
								m.getDeclaringClass().getName(), m.getName(), e);
			} catch (InvocationTargetException e) {
				log.error(e.getTargetException().getMessage(), e.getTargetException());
				log.error("ignoring the {} hook", m.getName());
			}
		}
	}

	private static Consumer<ServiceContext> createHook(Method m) {
		return (ServiceContext s) -> {
			try {
				m.invoke(objectsWithHooks.get(m.getDeclaringClass()), s);
			} catch (IllegalAccessException e) {
				log.error("could not execute hook {} of class {} for service {} version{} because the " +
												"hook method is not accessible", m.getName(),
								m.getDeclaringClass().getName(), s.getName(), s.getVersion());
			} catch (InvocationTargetException e) {
				log.error("could not execute hook {} of class {} for service {} version {},cause:",
								m.getName(), m.getDeclaringClass().getName(), s.getName(), s.getVersion(),
								e.getTargetException());
			}
		};
	}
}
