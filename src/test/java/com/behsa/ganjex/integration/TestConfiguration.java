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

package com.behsa.ganjex.integration;

import com.behsa.ganjex.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * test implementation of the {@link Configuration}, which load config-test.properties file
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
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
