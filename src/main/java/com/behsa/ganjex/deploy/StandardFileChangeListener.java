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

package com.behsa.ganjex.deploy;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.lifecycle.LifecycleManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * The <b>StandardFileChangeListener</b> class is responsible to deploy services, assign classloader
 * to them, and start services by calling all the startup hooks registered by the libraries.
 * by implementing <link {@link FileChangeListener}> an instance of this class should be assign
 * to {@link JarWatcher} constructor as a listener.
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class StandardFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(StandardFileChangeListener.class);
	private LifecycleManagement lifecycleManagement;
	private ClassLoader parentClassLoader;

	public StandardFileChangeListener(ClassLoader parentClassLoader, LifecycleManagement
					lifecycleManagement) {
		this.parentClassLoader = parentClassLoader;
		this.lifecycleManagement = lifecycleManagement;
	}

	@Override
	public void fileAdd(File jar) {
		fileRemoved(jar);
		log.info("new service found {}", jar.getName());
		URL jarUrl;
		try {
			jarUrl = jar.toURI().toURL();
		} catch (MalformedURLException e) {
			log.error("could not load {}", jar.getAbsolutePath(), e);
			return;
		}
		ClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, parentClassLoader);
		try {
			ServiceContext context = new ServiceContext(jar.getName(), classLoader);
			lifecycleManagement.serviceStarted(context);
		} catch (FileNotFoundException e) {
			log.error("could not load manifest.service in {}", jar.getName());
		} catch (IOException e) {
			log.error("could not start service {} cause: ", jar.getName());
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void fileRemoved(File f) {
		ServiceContext context = lifecycleManagement.findContext(f.getName());
		if (Objects.nonNull(context))
			lifecycleManagement.serviceDestroyed(context);
	}
}
