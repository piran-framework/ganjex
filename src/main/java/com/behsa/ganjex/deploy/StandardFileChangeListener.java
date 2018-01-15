package com.behsa.ganjex.deploy;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.bootstrap.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/**
 * The <b>StandardFileChangeListener</b> class is responsible to deploy services, assign classloader
 * to them, and start services by calling all the startup hooks registered by the libraries.
 * by implementing <link {@link FileChangeListener}> an instance of this class should be assign
 * to {@link JarWatcher} constructor as a listener.
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class StandardFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(StandardFileChangeListener.class);


	@Override
	public void fileModified(File jar) {
		log.info("new application found {}", jar.getName());
		URL jarUrl = null;
		try {
			jarUrl = new URL("file://" + jar.getAbsolutePath());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl},
						Bootstrap.libClassLoader());
		new Thread(() -> {
			try {
				Thread.currentThread().setContextClassLoader(classLoader);
				Properties manifest = new Properties();
				manifest.load(classLoader.getResourceAsStream("manifest.properties"));
				ServiceContext context = new ServiceContext(jar.getName(),
								Integer.parseInt(manifest.getProperty("version")),
								Thread.currentThread(),
								classLoader, manifest);
				Bootstrap.lifecycleManagement().serviceStarted(context);
			} catch (IOException e) {
				log.error("could not start service {} cause: ", jar.getName());
				log.error(e.getMessage(), e);
			}
		}).start();
	}

	@Override
	public void fileRemoved(File f) {

	}
}
