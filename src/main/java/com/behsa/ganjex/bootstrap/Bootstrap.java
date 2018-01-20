package com.behsa.ganjex.bootstrap;

import com.behsa.ganjex.config.Config;
import com.behsa.ganjex.config.StandardConfigurationLoader;
import com.behsa.ganjex.deploy.JarWatcher;
import com.behsa.ganjex.deploy.StandardFileChangeListener;
import com.behsa.ganjex.lifecycle.LifecycleManagement;
import com.behsa.ganjex.util.JarFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;
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
	private static LifecycleManagement lifecycleManagement = LifecycleManagement.newInstance();
	private static JarWatcher jarWatcher = null;
	private volatile static boolean bootstrapped = false;

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

	/**
	 * indicate the bootstrap process of the container has done or not
	 *
	 * @return return true if and only if container bootstrapped
	 */
	public static boolean bootstraped() {
		return bootstrapped;
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


	private static void watchServicesDirectory(String servicePath) {
		jarWatcher = new JarWatcher(new File(servicePath), new StandardFileChangeListener());
	}

	/**
	 * useful for testing, destroy the container and clean all the states, also interrupt all
	 * watcher threads
	 */
	public static void destroy() {
		mainClassLoader = null;
		libClassLoader = null;
		lifecycleManagement.destroy();
		bootstrapped = false;
		jarWatcher.destroy();
		jarWatcher = null;
		System.gc();
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
		RegisterHookHelper.loadHooks();
		lifecycleManagement.doneRegistering();
		watchServicesDirectory(config().get("service.path"));
		bootstrapped = true;
	}
}
