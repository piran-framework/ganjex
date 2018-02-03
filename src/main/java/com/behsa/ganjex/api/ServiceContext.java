package com.behsa.ganjex.api;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Properties;

/**
 * immutable class represents the context information of the service
 * <p>
 * this class used by the hooks which libraries register them into ganjex, every hook receive an
 * instance of this class when a service start or destroy depends on a type of hook.
 * <p>
 * hooks can use specific classloader provided by the object of this class to scan the service
 * code and find some specific point they want to take action against
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class ServiceContext {
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
						", classLoader=" + classLoader +
						", manifest=" + manifest +
						'}';
	}
}
