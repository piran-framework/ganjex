package com.behsa.ganjex.bootstrap;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.StartupHook;
import com.behsa.ganjex.config.Config;
import com.behsa.ganjex.config.StandardConfigurationLoader;
import com.behsa.ganjex.deploy.JarWatcher;
import com.behsa.ganjex.deploy.StandardFileChangeListener;
import com.behsa.ganjex.lifecycle.LifecycleManagement;
import com.behsa.ganjex.util.JarFilter;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.behsa.ganjex.config.Config.config;

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
public class Bootstrap {
	private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
	private static ClassLoader mainClassLoader;
	private static ClassLoader libClassLoader;
	private static LifecycleManagement lifecycleManagement;

	/**
	 * @return the top level classloader
	 */
	public static ClassLoader mainClassLoader() {
		return mainClassLoader;
	}

	/**
	 * @return library classloader
	 */
	public static ClassLoader libClassLoader() {
		return libClassLoader;
	}

	/**
	 * {@link LifecycleManagement} instance created in the bootstrapping phase. this instance
	 * created in the library classloader and all the hooks of libraries registered to it
	 *
	 * @return lifecycleManagement instance
	 */
	public static LifecycleManagement lifecycleManagement() {
		return lifecycleManagement;
	}

	private static void loadLibraries(String libraryPath) {
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

	private static void loadHooks() {
		Thread.currentThread().setContextClassLoader(libClassLoader);
		lifecycleManagement = new LifecycleManagement();
		Reflections reflections = new Reflections("com.behsa", new MethodAnnotationsScanner());
		Set<Method> startupHookMethods = reflections.getMethodsAnnotatedWith(StartupHook.class);
		Set<Class<?>> classesOfHooks = new HashSet<>();
		startupHookMethods.forEach((Method m) -> {
			if (!classesOfHooks.contains(m.getDeclaringClass())) {
				classesOfHooks.add(m.getDeclaringClass());
			}
			try {
				Constructor<?> constructor = m.getDeclaringClass().getConstructor((Class<?>[]) null);
				Object hookClassObj = constructor.newInstance((Object[]) null);
				Consumer<ServiceContext> hook = (ServiceContext s) -> {
					try {
						m.invoke(hookClassObj, s);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				};
				lifecycleManagement.registerStartupHook(new com.behsa.ganjex.lifecycle.StartupHook(hook));
			} catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
							IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		//TODO: do the same for shutdown hooks
		lifecycleManagement.doneRegistering();
	}

	private static void watchServicesDirectory(String servicePath) {
		new JarWatcher(new File(servicePath), new StandardFileChangeListener());
	}

	/**
	 * start point of the ganjex container
	 *
	 * @param args it could be a relative or absolute path of the config.properties file
	 */
	public static void main(String[] args) {
		Config.setConfig(new StandardConfigurationLoader(args));
		mainClassLoader = Bootstrap.class.getClassLoader();
		loadLibraries(config().get("lib.path"));
		loadHooks();
		watchServicesDirectory(config().get("service.path"));
	}
}
