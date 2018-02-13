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

package com.behsa.ganjex.integration.hooksExecution;

import com.behsa.ganjex.api.StartupHook;
import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;

/**
 * @author Esa Hekmatizadeh
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
