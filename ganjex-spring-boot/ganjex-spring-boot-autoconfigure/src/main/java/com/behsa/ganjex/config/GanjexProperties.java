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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hekmatof
 * @since 1.0
 */
@ConfigurationProperties("ganjex")
public class GanjexProperties {
	private String servicePath;
	private String libPath;
	private int watchDelay = 10;

	public String getServicePath() {
		return servicePath;
	}

	public GanjexProperties setServicePath(String servicePath) {
		this.servicePath = servicePath;
		return this;
	}

	public String getLibPath() {
		return libPath;
	}

	public GanjexProperties setLibPath(String libPath) {
		this.libPath = libPath;
		return this;
	}

	public int getWatchDelay() {
		return watchDelay;
	}

	public GanjexProperties setWatchDelay(int watchDelay) {
		this.watchDelay = watchDelay;
		return this;
	}
}
