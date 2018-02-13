/*
 * Copyright 2018 Behsa Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.behsa.ganjex.container;

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
 * {@link HookLoader} is a class responsible to load all hooks in the base package and
 * register all of them into {@link com.behsa.ganjex.lifecycle.LifecycleManagement} instance
 *
 * @author hekmatof
 * @since 1.0
 */
public final class HookLoader {
	private final Logger log = LoggerFactory.getLogger(HookLoader.class);
	/**
	 * container configuration and state object also useful to get <code>lifecycleManagement</code>
	 * instance
	 */
	private final GanjexApplication app;
	private final Map<Class<?>, Object> objectsWithHooks = new HashMap<>();

	/**
	 * construct a new <code>HookLoader</code> with the ganjex container instance passed to it
	 *
	 * @param app ganjex container state object
	 */
	public HookLoader(GanjexApplication app) {
		this.app = app;
	}

	/**
	 * Find all hooks under the base package and register all of them into the
	 * lifecycleManagement instance of the container
	 */
	public void loadHooks() {
		//change the logger of the reflections
		Reflections.log = LoggerFactory.getLogger(Reflections.class);
		Reflections libraries = new Reflections(app.config().getBasePackage(),
						new MethodAnnotationsScanner(), app.mainClassLoader());
		Set<Method> startupHookMethods = libraries.getMethodsAnnotatedWith(StartupHook.class);
		Set<Method> shutdownHooksMethods = libraries.getMethodsAnnotatedWith(ShutdownHook.class);
		startupHookMethods.forEach(this::addDeclaringClass);
		shutdownHooksMethods.forEach(this::addDeclaringClass);
		startupHookMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
						.forEach(method -> app.lifecycleManagement().registerStartupHook(
										new com.behsa.ganjex.lifecycle.StartupHook(createHook(method),
														method.getAnnotationsByType(StartupHook.class)[0].priority())));
		shutdownHooksMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
						.forEach(method -> app.lifecycleManagement().registerShutdownHook(
										new com.behsa.ganjex.lifecycle.ShutdownHook(createHook(method),
														method.getAnnotationsByType(ShutdownHook.class)[0].priority()
										)));
	}

	private void addDeclaringClass(Method m) {
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

	private Consumer<ServiceContext> createHook(Method m) {
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
