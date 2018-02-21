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
 * This class is used by the hooks, every hook receives an instance of this class as soon as a service
 * is created or destroyed based on the type of hook.
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
