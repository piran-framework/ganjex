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
