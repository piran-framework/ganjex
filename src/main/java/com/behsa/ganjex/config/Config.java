package com.behsa.ganjex.config;

import java.util.Objects;

/**
 * static class provide static access to the Configuration object
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class Config {
	private static Configuration config;

	public static void setConfig(Configuration configuration) {
		config = configuration;
	}

	public static Configuration config() {
		if (Objects.isNull(config))
			throw new IllegalStateException("config is null, setConfig method should call before access" +
							" to config");
		return config;
	}

	private Config() {
	}

}
