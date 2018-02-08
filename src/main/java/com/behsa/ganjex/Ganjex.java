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

package com.behsa.ganjex;

import com.behsa.ganjex.api.GanjexConfiguration;
import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import com.behsa.ganjex.api.StartupHook;
import com.behsa.ganjex.deploy.JarFilter;
import com.behsa.ganjex.deploy.JarWatcher;
import com.behsa.ganjex.deploy.StandardFileChangeListener;
import com.behsa.ganjex.lifecycle.LifecycleManagement;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * bootstrap static class of the ganjex to start the container
 * <p>
 * the bootstrap process assign main classloader which is top level classloader ganjex use it
 * itself. there is a lib classloader which is responsible to load all the library jar files from
 * specified path. the lib classloader parent is main classloader and it is parent of each
 * service classloader.
 * </p>
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class Ganjex {
	private static final Logger log = LoggerFactory.getLogger(Ganjex.class);
	private static volatile boolean bootstrapped = false;
	private GanjexConfiguration config;
	private ClassLoader mainClassLoader;
	private ClassLoader libClassLoader;
	private LifecycleManagement lifecycleManagement = new LifecycleManagement();
	private JarWatcher jarWatcher = null;
	private RegisterHookHelper registerHookHelper;

	public Ganjex(GanjexConfiguration config) {
		this.config = config;
		registerHookHelper = new RegisterHookHelper();
	}

	/**
	 * start ganjex container
	 *
	 * @param config configuration object to create ganjex container
	 */
	public static Ganjex run(GanjexConfiguration config) {
		return new Ganjex(config).run();
	}

	/**
	 * indicate the bootstrap process of the container has done or not
	 *
	 * @return return true if and only if container bootstrapped
	 */
	public static boolean bootstraped() {
		return bootstrapped;
	}

	public GanjexConfiguration getConfig() {
		return config;
	}

	/**
	 * @return the top level classloader
	 */
	public ClassLoader mainClassLoader() {
		return mainClassLoader;
	}

	/**
	 * @return library classloader
	 */
	public ClassLoader libClassLoader() {
		return libClassLoader;
	}

	/**
	 * {@link LifecycleManagement} instance created in the bootstrapping phase. this instance
	 * created in the library classloader and all the hooks of libraries registered to it
	 *
	 * @return lifecycleManagement instance
	 */
	public LifecycleManagement lifecycleManagement() {
		return lifecycleManagement;
	}

	private void loadLibraries(String libraryPath) {
		log.debug("loading the libraries...");
		File file = new File(libraryPath);
		if (!file.isDirectory()) {
			log.error("lib.path property is not correctly set. it does not point to a directory");
			System.exit(1);
		}
		File[] jars = file.listFiles(new JarFilter());
		if (Objects.isNull(jars))
			return;
		log.debug("libraries: {}",
						Arrays.stream(jars).map(File::getName).collect(Collectors.joining(", ")));
		URL[] urls = Arrays.stream(jars).map(jar -> "file://" + jar.getAbsolutePath())
						.map(spec -> {
							try {
								return new URL(spec);
							} catch (MalformedURLException e) {
								log.error("could not load {}", spec);
								return null;
							}
						}).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new URL[0]);
		libClassLoader = new URLClassLoader(urls, mainClassLoader);
		log.debug("loading library completed");
	}

	private void watchServicesDirectory(String servicePath) {
		jarWatcher = new JarWatcher(new File(servicePath),
						new StandardFileChangeListener(libClassLoader, lifecycleManagement),
						config.getWatcherDelay());
	}

	/**
	 * useful for testing, destroy the container and clean all the states, also interrupt all
	 * watcher threads
	 */
	public void destroy() throws InterruptedException {
		libClassLoader = null;
		lifecycleManagement.destroy();
		bootstrapped = false;
		if (Objects.nonNull(jarWatcher))
			jarWatcher.destroy();
		System.gc();
		log.info("ganjex shutdown correctly");
	}

	public Ganjex run() {
		mainClassLoader = Ganjex.class.getClassLoader();
		loadLibraries(config.getLibPath());
		registerHookHelper.loadHooks(config.getBasePackage());
		lifecycleManagement.doneRegistering();
		watchServicesDirectory(config.getServicePath());
		bootstrapped = true;
		return this;
	}

	/**
	 * bootstrapping helper class to register all libraries hooks into lifecycleManagement
	 *
	 * @author Esa Hekmatizadeh
	 * @version 1.0
	 */
	private class RegisterHookHelper {
		private final Logger log = LoggerFactory.getLogger(RegisterHookHelper.class);

		private Map<Class<?>, Object> objectsWithHooks = new HashMap<>();


		void loadHooks(String basePackage) {
			Reflections.log = LoggerFactory.getLogger(Reflections.class);
			Reflections libraries = new Reflections(basePackage, new MethodAnnotationsScanner(),
							mainClassLoader());
			Set<Method> startupHookMethods = libraries.getMethodsAnnotatedWith(StartupHook.class);
			Set<Method> shutdownHooksMethods = libraries.getMethodsAnnotatedWith(ShutdownHook.class);
			startupHookMethods.forEach(this::addDeclaringClass);
			shutdownHooksMethods.forEach(this::addDeclaringClass);
			startupHookMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
							.forEach(method -> lifecycleManagement().registerStartupHook(
											new com.behsa.ganjex.lifecycle.StartupHook(createHook(method))));
			shutdownHooksMethods.stream().filter(m -> objectsWithHooks.containsKey(m.getDeclaringClass()))
							.forEach(method -> lifecycleManagement().registerShutdownHook(
											new com.behsa.ganjex.lifecycle.ShutdownHook(createHook(method))));
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

}
