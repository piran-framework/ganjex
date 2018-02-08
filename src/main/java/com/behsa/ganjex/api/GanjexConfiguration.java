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

package com.behsa.ganjex.api;

/**
 * @author Esa Hekmatizadeh
 */
public class GanjexConfiguration {
	private final String libPath;
	private final String servicePath;
	private final String basePackage;
	private final long watcherDelay;//in second

	private GanjexConfiguration(Builder builder) {
		this.libPath = builder.libPath;
		this.servicePath = builder.servicePath;
		this.basePackage = builder.basePackage;
		this.watcherDelay = builder.watcherDelay;
	}

	public String getLibPath() {
		return libPath;
	}

	public String getServicePath() {
		return servicePath;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public long getWatcherDelay() {
		return watcherDelay;
	}

	public static class Builder {
		private String libPath;
		private String servicePath;
		private String basePackage;
		private long watcherDelay;//in second

		public Builder libPath(String libPath) {
			this.libPath = libPath;
			return this;
		}

		public Builder servicePath(String servicePath) {
			this.servicePath = servicePath;
			return this;
		}

		public Builder basePackage(String basePackage) {
			this.basePackage = basePackage;
			return this;
		}

		public Builder watcherDelay(long watcherDelay) {
			this.watcherDelay = watcherDelay;
			return this;
		}

		public GanjexConfiguration build() {
			return new GanjexConfiguration(this);
		}

	}
}
