/*
 * Copyright (c) 2018 Behsa Corporation.
 *
 *   This file is part of Ganjex.
 *
 *    Ganjex is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Ganjex is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Ganjex.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.behsacorp.ganjex.api;

import com.behsacorp.ganjex.container.GanjexApplication;
import com.behsacorp.ganjex.container.HookLoader;
import com.behsacorp.ganjex.watch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

/**
 * Ganjex container class whose instance obviously represents a Ganjex container.
 * <p>
 * The static method <code>run({@link GanjexConfiguration})</code> is expected to be invoked to
 * start a new container. By doing so, Ganjex would watch the library and service
 * directory for any prospective changes. As soon as a jar file is added to or removed from the
 * directory, Ganjex would start or shutdown services pertinent to the altered jar file.
 * </p>
 *
 * @author hekmatof
 * @since 1.0
 */
public final class Ganjex {
  private static final Logger log = LoggerFactory.getLogger(Ganjex.class);
  private static volatile boolean bootstrapped = false;
  private final GanjexApplication app;
  private JarWatcher serviceWatcher = null;
  private ClasspathWatcher classpathWatcher = null;
  private JarWatcher libWatcher = null;

  /**
	 * Creates a new Ganjex container. In order to run the container method <code>run</code>
	 * should be called.
	 *
	 * @param config ganjex application configuration instance
	 */
	@SuppressWarnings("WeakerAccess")
	public Ganjex(GanjexConfiguration config) {
		app = new GanjexApplication(config);
	}

  /**
	 * Starts a new Ganjex container and returns an object of this class representing the container.
	 * same as calling <code>new Ganjex(config).run()</code>
	 *
	 * @param config ganjex configuration object, this object should be created by the
	 *               {@link GanjexConfiguration.Builder} which is a builder for
	 *               {@link GanjexConfiguration}
	 * @return a running Ganjex container object
	 */
	public static Ganjex run(GanjexConfiguration config) {
		return new Ganjex(config).run();
	}
	
	/**
	 * Indicates whether the bootstrap process of the container has been done or not.
	 *
	 * @return {@code true} if and only if the container has been bootstrapped but
	 * has not been destroyed.
	 */
	public static boolean bootstrapped() {
		return bootstrapped;
	}

	/**
	 * @return The top level classloader.
	 */
	public ClassLoader mainClassLoader() {
		return app.mainClassLoader();
	}

  private void watchServicesDirectory() {
    classpathWatcher = new ClasspathWatcher(app.config().getClassPaths(),
        new ClassPathFileChangeListener(app), app.config().getWatcherDelay());
    serviceWatcher = new JarWatcher(new File(app.config().getServicePath()),
        new ServiceFileChangeListener(app), app.config().getWatcherDelay());
  }

  private void watchLibraryDirectory() {
    libWatcher = new JarWatcher(new File(app.config().getLibPath()),
        new LibraryFileChangeListener(app), app.config().getWatcherDelay());
  }

	/**
	 * Useful method for testing, which destroys the container, cleans all the states and interrupts
	 * all watcher threads.
	 */
	public void destroy(){
		app.destroy();
		bootstrapped = false;
		if (Objects.nonNull(serviceWatcher))
			serviceWatcher.destroy();
    if (Objects.nonNull(classpathWatcher))
      classpathWatcher.destroy();
		if (Objects.nonNull(libWatcher))
			libWatcher.destroy();
		System.gc();
		log.info("ganjex shutdown correctly");
	}

	/**
	 * Runs the container. Invokes threads to watch the library and service directory to detect any changes
	 * might happen. This method has been used in the complicated scenarios, usually clients use
	 * <code>Ganjex.run({@link GanjexConfiguration})</code> static method.
	 *
	 * @return Ganjex container object.
	 */
	@SuppressWarnings("WeakerAccess")
	public Ganjex run() {
		watchLibraryDirectory();
		if(app.libClassLoader()==null)
			app.setLibClassLoader(mainClassLoader());
		new HookLoader(app).loadHooks();
		app.lifecycleManagement().doneRegistering();
		watchServicesDirectory();
		bootstrapped = true;
		return this;
	}
}
