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

import java.io.File;
import java.util.Objects;

/**
 * An immutable class for shutdown(destroy) a service, it instantiated with the jar file of the
 * service and <code>destroy</code> method try to shutdown the service
 *
 * @author hekmatof
 * @see LifecycleManagement
 * @since 1.0
 */
public class ServiceDestroyer {
	public File jar;

	public ServiceDestroyer(File jar) {
		this.jar = jar;
	}

	/**
	 * try to destroy the service using the {@link LifecycleManagement} instance of the ganjex
	 * container
	 *
	 * @param app ganjex container instance
	 */
	public void destroy(GanjexApplication app) {
		ServiceContext context = app.lifecycleManagement().findContext(jar.getName());
		if (Objects.nonNull(context))
			app.lifecycleManagement().serviceDestroyed(context);
	}
}
