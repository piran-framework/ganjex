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

package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.container.GanjexApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * An immutable class for start(deploy) a new service, it instantiated with the jar file of the
 * service and libraries classloader and method <code>deploy</code> try to deploy the new service
 *
 * @author Esa Hekmatizadeh
 * @see LifecycleManagement
 * @since 1.0
 */
public class ServiceStarter {
	private static final Logger log = LoggerFactory.getLogger(ServiceStarter.class);
	private final File jar;
	private final ClassLoader libClassLoader;

	public ServiceStarter(File serviceFile, ClassLoader libClassLoader) {
		this.jar = serviceFile;
		this.libClassLoader = libClassLoader;
	}

	/**
	 * try to deploy the service using {@link LifecycleManagement} instance of the
	 * {@link GanjexApplication} object given
	 *
	 * @param app ganjex container instance
	 */
	public void deploy(GanjexApplication app) {
		URL jarUrl;
		try {
			jarUrl = jar.toURI().toURL();
		} catch (MalformedURLException e) {
			log.error("could not load {}", jar.getAbsolutePath(), e);
			return;
		}
		//create a dedicated classloader fot the service with libClassLoader parent
		ClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, libClassLoader);
		try {
			ServiceContext context = new ServiceContext(jar.getName(), classLoader);
			app.lifecycleManagement().serviceStarted(context);
		} catch (FileNotFoundException e) {
			log.error("could not load manifest.service in {}", jar.getName(), e);
		} catch (IOException e) {
			log.error("could not start service {} cause: ", jar.getName());
			log.error(e.getMessage(), e);
		}
	}
}
