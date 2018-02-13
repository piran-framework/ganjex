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

package com.behsa.ganjex.watch;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.container.GanjexApplication;
import com.behsa.ganjex.lifecycle.ServiceDestroyer;
import com.behsa.ganjex.lifecycle.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@link LibraryFileChangeListener} class by implementing {@link FileChangeListener} is a
 * listener of changes in the libraries directory. if it detect any changes in that directory it
 * try to stop all the services and reload libraries in the newly created libClassLoader and
 * restart all the services again.
 *
 * @author hekmatof
 * @see FileChangeListener
 * @since 1.0
 */
public class LibraryFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(LibraryFileChangeListener.class);
	private final GanjexApplication app;

	public LibraryFileChangeListener(GanjexApplication app) {
		this.app = app;
	}

	@Override
	public void fileAdd(File file) {
		reloadLibraries();
	}

	@Override
	public void fileRemoved(File file) {
		reloadLibraries();
	}

	private void reloadLibraries() {
		Collection<ServiceContext> services = app.lifecycleManagement().allServices();
		stopAllService(services);
		loadLibraries();
		startAllService(services);
	}

	private void startAllService(Collection<ServiceContext> servicesList) {
		servicesList.stream()
						.map(ServiceContext::getFileName)
						.map(name -> app.config().getServicePath() + File.separator + name)
						.map(File::new)
						.map(f -> new ServiceStarter(f, app.libClassLoader()))
						.forEach(d -> d.deploy(app));
	}

	private void stopAllService(Collection<ServiceContext> servicesList) {
		servicesList.stream()
						.map(ServiceContext::getFileName)
						.map(name -> app.config().getServicePath() + File.separator + name)
						.map(File::new)
						.map(ServiceDestroyer::new)
						.forEach(d -> d.destroy(app));
	}

	private void loadLibraries() {
		log.debug("loading the libraries...");
		File file = new File(app.config().getLibPath());
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
		app.setLibClassLoader(new URLClassLoader(urls, app.mainClassLoader()));
		log.debug("loading library completed");
	}
}
