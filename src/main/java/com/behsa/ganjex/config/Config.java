package com.behsa.ganjex.config;

import java.util.Objects;

/**
 * static class provide static access to the Configuration object
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public final class Config {
	private static Configuration config;

	private Config() throws IllegalAccessException {
		throw new IllegalAccessException("this method should be invoked");
	}

	public static void setConfig(Configuration configuration) {
		config = configuration;
	}

	public static Configuration config() {
		if (Objects.isNull(config))
			throw new IllegalStateException("config is null, setConfig method should call before access" +
							" to config");
		return config;
	}

}
