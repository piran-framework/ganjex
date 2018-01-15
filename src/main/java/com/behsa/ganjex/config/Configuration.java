package com.behsa.ganjex.config;

/**
 * Configuration interface, provide access methods to the configuration
 * the default implementation is {@link StandardConfigurationLoader}
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public interface Configuration {
	String get(String key);
}
