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

package com.behsa.ganjex.integration.multipleVersion;

import com.behsa.ganjex.api.StartupHook;
import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hekmatof
 */
public class CallServiceDirectlyLibrary {
	private static final Logger log = LoggerFactory.getLogger(CallServiceDirectlyLibrary.class);
	private static final Map<ServiceInfo, ServiceContext> contextMap = new ConcurrentHashMap<>();

	static void clear(){
		contextMap.clear();
	}

	public static long serviceCount(String name) {
		return contextMap.entrySet().stream().filter(s -> s.getKey().name.equals(name))
						.count();
	}

	public static int getVersion(String name, Integer version) {
		contextMap.keySet().forEach(System.out::println);
		ServiceInfo serviceInfo = new ServiceInfo(name, version);
		if (!contextMap.containsKey(serviceInfo))
			return -1;
		try {
			Class<?> versionServiceClass = contextMap.get(serviceInfo)
							.getClassLoader().loadClass("com.behsa.VersionService");
			Constructor<?> versionServiceConstructor = versionServiceClass.getConstructor((Class<?>[]) null);
			Object versionServiceObj = versionServiceConstructor.newInstance((Object[]) null);
			Method getVersionMethod = versionServiceClass.getMethod("getVersion", (Class<?>[]) null);
			return (int) getVersionMethod.invoke(versionServiceObj, (Object[]) null);
		} catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
						InvocationTargetException | ClassNotFoundException e) {
			log.error(e.getMessage(), e);
			return -2;
		}
	}

	@StartupHook
	public void startService(ServiceContext context) {
		contextMap.put(new ServiceInfo(context), context);
	}

	@ShutdownHook
	public void shutdownService(ServiceContext context) {
		contextMap.remove(new ServiceInfo(context));
	}

	private static class ServiceInfo {
		private final String name;
		private final int version;

		private ServiceInfo(ServiceContext context) {
			this.name = context.getName();
			this.version = context.getVersion();
		}

		public ServiceInfo(String name, int version) {
			this.name = name;
			this.version = version;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ServiceInfo that = (ServiceInfo) o;
			return version == that.version &&
							Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, version);
		}

		@Override
		public String toString() {
			return "ServiceInfo{" +
							"name='" + name + '\'' +
							", version=" + version +
							'}';
		}
	}
}