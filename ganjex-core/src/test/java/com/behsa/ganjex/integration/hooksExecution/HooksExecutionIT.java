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

package com.behsa.ganjex.integration.hooksExecution;

import com.behsa.ganjex.api.Ganjex;
import com.behsa.ganjex.api.GanjexConfiguration;
import com.behsa.ganjex.integration.TestUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.integration.TestUtil.*;
import static org.testng.Assert.assertEquals;

/**
 * @author hekmatof
 */
@Test(sequential = true)
public class HooksExecutionIT {

	@Test
	public void testEveryHooksRunOnce() throws IOException, InterruptedException,
					ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		clean();
		LibWithDifferentHooks.clean();
		Ganjex ganjex = Ganjex.run(new GanjexConfiguration.Builder()
						.libPath(libPath)
						.servicePath(servicePath)
						.watcherDelay(1)
						.hooks(new LibWithDifferentHooks())
						.build()
		);
		waitToBootstrap();
		int shutdownHookRunTime = LibWithDifferentHooks.getShutdownHookRunTime();
		assertEquals(shutdownHookRunTime, 0);
		deployService(TEST_PATH + "hooksExecution/simpleService/", "simple-service");
		Thread.sleep(TIMEOUT);
		int startHookRunTime = LibWithDifferentHooks.getStartHookRunTime();
		int startHook2RunTime = LibWithDifferentHooks.getStartHook2RunTime();
		assertEquals(startHookRunTime, 1);
		assertEquals(startHook2RunTime, 1);
		unDeployService("simple-service");
		Thread.sleep(TIMEOUT);
		shutdownHookRunTime = LibWithDifferentHooks.getShutdownHookRunTime();
		assertEquals(shutdownHookRunTime, 1);
		ganjex.destroy();
	}
}
