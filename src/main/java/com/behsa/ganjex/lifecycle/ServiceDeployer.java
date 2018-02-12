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

import com.behsa.ganjex.api.Ganjex;
import com.behsa.ganjex.api.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Esa Hekmatizadeh
 */
public class ServiceDeployer {
	private static final Logger log = LoggerFactory.getLogger(ServiceDeployer.class);
	private final File jar;
	private final ClassLoader libClassLoader;


	public ServiceDeployer(File serviceFile,ClassLoader libClassLoader) {
		this.jar = serviceFile;
		this.libClassLoader = libClassLoader;
	}

	public void deploy(Ganjex app) {
		URL jarUrl;
		try {
			jarUrl = jar.toURI().toURL();
		} catch (MalformedURLException e) {
			log.error("could not load {}", jar.getAbsolutePath(), e);
			return;
		}
		ClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, libClassLoader);
		try {
			ServiceContext context = new ServiceContext(jar.getName(), classLoader);
			app.lifecycleManagement().serviceStarted(context);
		} catch (FileNotFoundException e) {
			log.error("could not load manifest.service in {}", jar.getName(),e);
		} catch (IOException e) {
			log.error("could not start service {} cause: ", jar.getName());
			log.error(e.getMessage(), e);
		}
	}
}
