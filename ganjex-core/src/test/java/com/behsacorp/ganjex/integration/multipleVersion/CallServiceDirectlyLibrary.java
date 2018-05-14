/*
 * Copyright (c) 2018 Behsa Corporation.
 *
 *   This file is part of Ganjex.
 *
 *    Ganjex is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Ganjex is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Ganjex.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.behsacorp.ganjex.integration.multipleVersion;

import com.behsacorp.ganjex.api.ServiceContext;
import com.behsacorp.ganjex.api.ShutdownHook;
import com.behsacorp.ganjex.api.StartupHook;
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

  static void clear() {
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
          .getClassLoader().loadClass("com.behsacorp.VersionService");
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