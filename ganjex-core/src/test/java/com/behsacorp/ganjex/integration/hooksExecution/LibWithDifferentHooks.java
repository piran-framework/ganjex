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

package com.behsacorp.ganjex.integration.hooksExecution;

import com.behsacorp.ganjex.api.ServiceContext;
import com.behsacorp.ganjex.api.ShutdownHook;
import com.behsacorp.ganjex.api.StartupHook;

/**
 * @author hekmatof
 */
public class LibWithDifferentHooks {
  private static int runTime = 0;
  private static int startHookRunTime = 0;
  private static int startHook2RunTime = 0;
  private static int shutdownHookRunTime = 0;

  public LibWithDifferentHooks() {
    runTime++;
  }

  public static void clean() {
    startHookRunTime = 0;
    startHook2RunTime = 0;
    shutdownHookRunTime = 0;
    runTime = 0;
  }

  public static int getRunTime() {
    return runTime;
  }

  public static int getStartHookRunTime() {
    return startHookRunTime;
  }

  public static int getStartHook2RunTime() {
    return startHook2RunTime;
  }

  public static int getShutdownHookRunTime() {
    return shutdownHookRunTime;
  }

  @StartupHook
  public void startHook(ServiceContext context) {
    startHookRunTime++;
  }

  @StartupHook(priority = 90)
  public void startHook2(ServiceContext context) {
    startHook2RunTime++;
  }

  @ShutdownHook
  public void shutdownHook(ServiceContext context) {
    shutdownHookRunTime++;
  }


}
