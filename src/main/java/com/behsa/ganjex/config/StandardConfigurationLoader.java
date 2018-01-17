package com.behsa.ganjex.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * default implementation for the {@link Configuration} interface. it check for the main args of
 * the execution, config file in the same location as the execution and internal config
 * .properties(default one) and load properties by this order
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class StandardConfigurationLoader implements Configuration {
	private static final Logger log = LoggerFactory.getLogger(StandardConfigurationLoader.class);
	private Properties properties = new Properties();

	/**
	 * create standard {@link Configuration}
	 *
	 * @param mainArgs main method args passed by the user when running project
	 */
	public StandardConfigurationLoader(String[] mainArgs) {
		try {
			if (mainArgs.length > 0) {
				properties.load(new FileInputStream(mainArgs[0]));
			} else {
				//TODO should scan pwd directory and check the mainArgs and handle priority of the properties
				properties.load(StandardConfigurationLoader.class.getClassLoader().getResourceAsStream
								("config.properties"));
			}
		} catch (IOException e) {
			log.error("could not find config.properties file");
			System.exit(1);
		}
	}

	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}
}
