package com.behsa.ganjex.e2e;

import com.behsa.ganjex.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Esa Hekmatizadeh
 */
public class TestConfiguration implements Configuration {
	private static final Logger log = LoggerFactory.getLogger(TestConfiguration.class);
	private static final Properties props = new Properties();

	static {
		try {
			props.load(TestConfiguration.class.getClassLoader().getResourceAsStream("config-test" +
							".properties"));
		} catch (IOException e) {
			log.error("could not load config-test.properties");
			System.exit(1);
		}
	}

	@Override
	public String get(String key) {
		return props.getProperty(key);
	}
}
