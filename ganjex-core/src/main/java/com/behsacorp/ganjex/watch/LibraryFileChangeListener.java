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

package com.behsacorp.ganjex.watch;

import com.behsacorp.ganjex.api.ServiceContext;
import com.behsacorp.ganjex.container.GanjexApplication;
import com.behsacorp.ganjex.lifecycle.ServiceDestroyer;
import com.behsacorp.ganjex.lifecycle.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The {@link LibraryFileChangeListener} class by implementing {@link FileChangeListener} is a
 * listener of changes in the libraries directory. if it detect any changes in that directory it
 * try to stop all the services and reload libraries in the newly created libClassLoader and
 * restart all the services again.
 *
 * @author hekmatof
 * @see FileChangeListener
 * @since 1.0
 */
public class LibraryFileChangeListener implements FileChangeListener {
  private static final Logger log = LoggerFactory.getLogger(LibraryFileChangeListener.class);
  private final GanjexApplication app;

  public LibraryFileChangeListener(GanjexApplication app) {
    this.app = app;
  }

  @Override
  public void fileAdd(File file) {
    reloadLibraries();
  }

  @Override
  public void fileRemoved(File file) {
    reloadLibraries();
  }

  private void reloadLibraries() {
    Collection<ServiceContext> services = app.lifecycleManagement().allServices();
    stopAllService(services);
    loadLibraries();
    startAllService(services);
  }

  private void startAllService(Collection<ServiceContext> servicesList) {
    servicesList.stream()
        .map(ServiceContext::getFileName)
        .map(name -> app.config().getServicePath() + File.separator + name)
        .map(File::new)
        .map(f -> new ServiceStarter(f, app.libClassLoader()))
        .forEach(d -> d.deploy(app));
  }

  private void stopAllService(Collection<ServiceContext> servicesList) {
    servicesList.stream()
        .map(ServiceContext::getFileName)
        .map(name -> app.config().getServicePath() + File.separator + name)
        .map(File::new)
        .map(ServiceDestroyer::new)
        .forEach(d -> d.destroy(app));
  }

  private void loadLibraries() {
    log.debug("loading the libraries...");
    File file = new File(app.config().getLibPath());
    if (!file.isDirectory()) {
      log.error("lib.path property is not correctly set. it does not point to a directory");
      System.exit(1);
    }
    File[] jars = file.listFiles(new JarFilter());
    if (Objects.isNull(jars))
      return;
    log.debug("libraries: {}",
        Arrays.stream(jars).map(File::getName).collect(Collectors.joining(", ")));
    URL[] urls = Arrays.stream(jars).map(jar -> "file://" + jar.getAbsolutePath())
        .map(spec -> {
          try {
            return new URL(spec);
          } catch (MalformedURLException e) {
            log.error("could not load {}", spec);
            return null;
          }
        }).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new URL[0]);
    app.setLibClassLoader(new URLClassLoader(urls, app.mainClassLoader()));
    log.debug("loading library completed");
  }
}
