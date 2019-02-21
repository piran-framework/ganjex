/*
 * Copyright (c) 2018 Isa Hekmatizadeh.
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

package com.piran.ganjex.lifecycle;

import com.piran.ganjex.api.ServiceContext;
import com.piran.ganjex.container.GanjexApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * An immutable class for start(deploy) a new service, it instantiated with the jar file of the
 * service and libraries classloader and method <code>deploy</code> try to deploy the new service
 *
 * @author hekmatof
 * @see LifecycleManagement
 * @since 1.0
 */
public final class ServiceStarter {
  private static final Logger log = LoggerFactory.getLogger(ServiceStarter.class);
  private final File jar;
  private final ClassLoader libClassLoader;

  public ServiceStarter(File serviceFile, ClassLoader libClassLoader) {
    this.jar = serviceFile;
    this.libClassLoader = libClassLoader;
  }

  /**
   * try to deploy the service using {@link LifecycleManagement} instance of the
   * {@link GanjexApplication} object given
   *
   * @param app ganjex container instance
   */
  public void deploy(GanjexApplication app) {
    URL jarUrl;
    try {
      jarUrl = jar.toURI().toURL();
    } catch (MalformedURLException e) {
      log.error("could not load {}", jar.getAbsolutePath(), e);
      return;
    }
    //create a dedicated classloader fot the service with libClassLoader parent
    ClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, libClassLoader);
    try {
      ServiceContext context = new ServiceContext(jar.getName(), classLoader);
      app.lifecycleManagement().serviceStarted(context);
    } catch (FileNotFoundException e) {
      log.error("could not load manifest.service in {}", jar.getName(), e);
    } catch (IOException e) {
      log.error("could not start service {} cause: ", jar.getName());
      log.error(e.getMessage(), e);
    }
  }
}
