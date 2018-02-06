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

package com.behsa.ganjex.util;

import java.util.concurrent.*;

/**
 * This static class is a utility to keep Executors and provide access to them
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class Executors {
	private static ScheduledExecutorService scheduled = new ScheduledThreadPoolExecutor(1);

	private Executors() throws IllegalAccessException {
		throw new IllegalAccessException("this method should not be invoked");
	}

	public static ScheduledExecutorService scheduledExecutor() {
		return scheduled;
	}


	public static void destroy() throws InterruptedException {
		scheduled.awaitTermination(1, TimeUnit.SECONDS);
		if (!scheduled.isTerminated())
			scheduled.shutdownNow();
		scheduled = new ScheduledThreadPoolExecutor(1);

	}
}
