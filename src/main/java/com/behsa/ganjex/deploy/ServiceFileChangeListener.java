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

import com.behsa.ganjex.api.Ganjex;
import com.behsa.ganjex.lifecycle.LibraryManager;
import com.behsa.ganjex.lifecycle.ServiceDeployer;
import com.behsa.ganjex.lifecycle.ServiceDestroyer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The <b>ServiceFileChangeListener</b> class is responsible to deploy services, assign classloader
 * to them, and start services by calling all the startup hooks registered by the libraries.
 * by implementing <link {@link FileChangeListener}> an instance of this class should be assign
 * to {@link JarWatcher} constructor as a listener.
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class ServiceFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(ServiceFileChangeListener.class);
	private final Ganjex app;
	private final LibraryManager libraryManager;

	public ServiceFileChangeListener(Ganjex ganjex, LibraryManager libraryManager) {
		this.app = ganjex;
		this.libraryManager = libraryManager;
	}

	@Override
	public void fileAdd(File jar) {
		log.info("new service found {}", jar.getName());
		fileRemoved(jar);
		new ServiceDeployer(jar,libraryManager.getLibClassLoader()).deploy(app);
	}

	@Override
	public void fileRemoved(File jar) {
		log.info("service {} is removed", jar.getName());
		new ServiceDestroyer(jar).destroy(app);
	}
}
