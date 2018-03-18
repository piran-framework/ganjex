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

package com.behsa.ganjex.api;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Properties;

/**
 * This is an immutable class representing the context information of the service.
 * <p>
 * This class is used by the hooks, every hook receives an instance of this class as soon as a
 * service is created or destroyed based on the type of hook.
 * <p>
 * Hooks will use specific classloader provided by the object of this class to scan the service
 * code and find specific points needed to be taken into consideration.
 *
 * @author hekmatof
 * @since 1.0
 */
public final class ServiceContext {
	private final String fileName;
	private final String name;
	private final int version;
	private final ClassLoader classLoader;
	private final Properties manifest = new Properties();

	public ServiceContext(String fileName, ClassLoader classLoader) throws IOException {
		URL resource = classLoader.getResource("manifest.properties");
		if (Objects.isNull(resource))
			throw new FileNotFoundException("manifest.properties");
		URLConnection urlConnection = resource.openConnection();
		urlConnection.setUseCaches(false);
		manifest.load(urlConnection.getInputStream());
		this.fileName = fileName;
		this.name = manifest.getProperty("name");
		//TODO:handle exception
		this.version = Integer.parseInt(manifest.getProperty("version"));
		this.classLoader = classLoader;
	}

	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public Properties getManifest() {
		return manifest;
	}

	@Override
	public String toString() {
		return "ServiceContext{" +
						"fileName='" + fileName + '\'' +
						", name='" + name + '\'' +
						", version=" + version +
						'}';
	}
}
