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
 * @author hekmatof
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
