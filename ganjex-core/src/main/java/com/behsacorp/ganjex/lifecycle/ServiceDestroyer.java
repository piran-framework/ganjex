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

package com.behsacorp.ganjex.lifecycle;

import com.behsacorp.ganjex.api.ServiceContext;
import com.behsacorp.ganjex.container.GanjexApplication;

import java.io.File;
import java.util.Objects;

/**
 * An immutable class for shutdown(destroy) a service, it instantiated with the jar file of the
 * service and <code>destroy</code> method try to shutdown the service
 *
 * @author hekmatof
 * @see LifecycleManagement
 * @since 1.0
 */
public final class ServiceDestroyer {
  private final File jar;

  private ServiceDestroyer() throws IllegalAccessException {
    throw new IllegalAccessException("Service Destroyer is not supposed to be instantiated reflectively.");
  }

  public ServiceDestroyer(File jar) {
    this.jar = jar;
  }

  /**
   * try to destroy the service using the {@link LifecycleManagement} instance of the ganjex
   * container
   *
   * @param app ganjex container instance
   */
  public void destroy(GanjexApplication app) {
    ServiceContext context = app.lifecycleManagement().findContext(jar.getName());
    if (Objects.nonNull(context))
      app.lifecycleManagement().serviceDestroyed(context);
  }
}
