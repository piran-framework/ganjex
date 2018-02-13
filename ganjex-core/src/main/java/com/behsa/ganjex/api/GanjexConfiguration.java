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
 * Immutable class stores the configurations needed by the ganjex container. an instance of this
 * class should passed to the <code>{@link Ganjex}.run(GanjexConfiguration) </code> static method
 * or the {@link Ganjex} constructor itself. use {@link GanjexConfiguration.Builder} class to
 * create a instance of {@link GanjexConfiguration}
 *
 * @author hekmatof
 * @since 1.0
 */
public final class GanjexConfiguration {
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

	/**
	 * @return path of the directory where libraries jar files resort there
	 */
	public String getLibPath() {
		return libPath;
	}

	/**
	 * @return path of the directory where services jar files resort there
	 */
	public String getServicePath() {
		return servicePath;
	}

	/**
	 * @return base package to scan to find hook annotations
	 */
	public String getBasePackage() {
		return basePackage;
	}

	/**
	 * @return file watchers delay in second
	 */
	public long getWatcherDelay() {
		return watcherDelay;
	}

	/**
	 * default builder of the {@link GanjexConfiguration}
	 */
	public static class Builder {
		private String libPath;
		private String servicePath;
		private String basePackage;
		private long watcherDelay;//in second

		/**
		 * @param libPath path of the directory where libraries jar files resort there
		 * @return builder itself
		 */
		public Builder libPath(String libPath) {
			this.libPath = libPath;
			return this;
		}

		/**
		 * @param servicePath path of the directory where services jar files resort there
		 * @return builder itself
		 */
		public Builder servicePath(String servicePath) {
			this.servicePath = servicePath;
			return this;
		}

		/**
		 * @param basePackage base package to scan to find hook annotations
		 * @return builder itself
		 */
		public Builder basePackage(String basePackage) {
			this.basePackage = basePackage;
			return this;
		}

		/**
		 * @param watcherDelay file watchers delay in second
		 * @return builder itself
		 */
		public Builder watcherDelay(long watcherDelay) {
			this.watcherDelay = watcherDelay;
			return this;
		}

		/**
		 * create the {@link GanjexConfiguration} instance and return it
		 *
		 * @return {@link GanjexConfiguration} instance created by the builder properties
		 */
		public GanjexConfiguration build() {
			return new GanjexConfiguration(this);
		}

	}
}
