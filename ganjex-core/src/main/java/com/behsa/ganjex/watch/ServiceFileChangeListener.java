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

import com.behsa.ganjex.container.GanjexApplication;
import com.behsa.ganjex.lifecycle.ServiceDestroyer;
import com.behsa.ganjex.lifecycle.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The <b>ServiceFileChangeListener</b> class by implementing {@link FileChangeListener} is a
 * listener of changes in the services directory. This listener create {@link ServiceStarter} and
 * {@link ServiceDestroyer} instance for each service added(or modified) or removed from the
 * directory and call the deploy or destroy method of that objects
 * <p>
 * An instance of this class should be assign
 * to {@link JarWatcher} constructor as a listener.
 *
 * @author hekmatof
 * @see ServiceStarter
 * @see ServiceDestroyer
 * @see FileChangeListener
 * @since 1.0
 */
public class ServiceFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(ServiceFileChangeListener.class);
	private final GanjexApplication app;

	public ServiceFileChangeListener(GanjexApplication app) {
		this.app = app;
	}

	@Override
	public void fileAdd(File jar) {
		log.info("new service found {}", jar.getName());
		fileRemoved(jar);
		new ServiceStarter(jar, app.libClassLoader()).deploy(app);
	}

	@Override
	public void fileRemoved(File jar) {
		log.info("service {} is removed", jar.getName());
		new ServiceDestroyer(jar).destroy(app);
	}
}
