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

import com.behsacorp.ganjex.container.GanjexApplication;
import com.behsacorp.ganjex.lifecycle.ServiceDestroyer;
import com.behsacorp.ganjex.lifecycle.ServiceStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The <b>ServiceFileChangeListener</b> class by implementing {@link FileChangeListener} is a
 * listener of changes in the services directory. This listener create {@link ServiceStarter} and
 * {@link ServiceDestroyer} instance for each service added(or modified) or removed from the
 * directory and call the deploy or destroy method of that objects
 * <p>
 * An instance of this class should be assign
 * to {@link JarWatcher} constructor as a listener.
 *
 * @author hekmatof
 * @see ServiceStarter
 * @see ServiceDestroyer
 * @see FileChangeListener
 * @since 1.0
 */
public class ServiceFileChangeListener implements FileChangeListener {
  private static final Logger log = LoggerFactory.getLogger(ServiceFileChangeListener.class);
  private final GanjexApplication app;

  public ServiceFileChangeListener(GanjexApplication app) {
    this.app = app;
  }

  @Override
  public void fileAdd(File jar) {
    log.info("new service found {}", jar.getName());
    new ServiceDestroyer(jar).destroy(app);
    new ServiceStarter(jar, app.libClassLoader()).deploy(app);
  }

  @Override
  public void fileRemoved(File jar) {
    log.info("service {} is removed", jar.getName());
    new ServiceDestroyer(jar).destroy(app);
  }
}
