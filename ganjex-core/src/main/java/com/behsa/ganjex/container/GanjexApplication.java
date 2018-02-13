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

package com.behsa.ganjex.container;

import com.behsa.ganjex.api.GanjexConfiguration;
import com.behsa.ganjex.lifecycle.LifecycleManagement;

/**
 * The objects of this class contain all of the ganjex application states and necessary
 * internal dependencies
 * <p>
 * {@link GanjexApplication} Object also responsible to manage the libraries classLoader which
 * should be updated after a change in the libraries detected
 *
 * @author Esa Hekmatizadeh
 */
public final class GanjexApplication {
	private final GanjexConfiguration config;
	private final LifecycleManagement lifecycleManagement = new LifecycleManagement();
	private final ClassLoader mainClassLoader;
	private ClassLoader libClassLoader;

	public GanjexApplication(GanjexConfiguration config) {
		this.config = config;
		this.mainClassLoader = GanjexApplication.class.getClassLoader();
	}

	/**
	 * Getter for the configuration instance which include all of the configuration needed by the
	 * Ganjex container
	 *
	 * @return the configuration instance
	 */
	public GanjexConfiguration config() {
		return config;
	}

	/**
	 * Getter for the {@link LifecycleManagement} instance created in the bootstrapping phase.
	 *
	 * @return lifecycleManagement instance
	 */
	public LifecycleManagement lifecycleManagement() {
		return lifecycleManagement;
	}

	/**
	 * Getter for the top level classloader
	 *
	 * @return the top level classloader
	 */
	public ClassLoader mainClassLoader() {
		return mainClassLoader;
	}

	/**
	 * Getter for the libraries classLoader
	 *
	 * @return libraries classloader
	 */
	public ClassLoader libClassLoader() {
		return libClassLoader;
	}

	/**
	 * change the libraries classloader
	 *
	 * @param libClassLoader new libraries classloader
	 */
	public void setLibClassLoader(ClassLoader libClassLoader) {
		this.libClassLoader = libClassLoader;
	}

	/**
	 * destroy the object, useful for testing purpose
	 */
	public void destroy() {
		this.lifecycleManagement.destroy();
	}
}
