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

package com.behsacorp.ganjex.integration.dynamicLibrary;

import com.behsacorp.ganjex.api.Ganjex;
import com.behsacorp.ganjex.api.GanjexConfiguration;
import com.behsacorp.ganjex.integration.TestUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsacorp.ganjex.integration.TestUtil.*;

/**
 * @author hekmatof
 */
@Test(sequential = true)
public class DynamicLibraryIT {
	private Ganjex ganjex;

	@Test
	public void testDynamicLibrary() throws IOException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		clean();
		ganjex = Ganjex.run(new GanjexConfiguration.Builder()
						.libPath(TestUtil.libPath)
						.servicePath(TestUtil.servicePath)
						.watcherDelay(1)
						.hooks(new FrameworkHook())
						.build());
		deployLib(TEST_PATH + "dynamicLibrary/dynamicLib/", "dynamic-lib");
		Thread.sleep(TIMEOUT);
		deployService(TEST_PATH + "dynamicLibrary/someService/", "service1");
		Thread.sleep(TIMEOUT);
		Assert.assertEquals(FrameworkHook.invokeMethodOnService(), "hello world");
		Thread.sleep(TIMEOUT);
		deployLib(TEST_PATH + "dynamicLibrary/dynamicLib2/", "dynamic-lib");
		Thread.sleep(TIMEOUT);
		Assert.assertEquals(FrameworkHook.invokeMethodOnService(), "hello world2");
	}

	@AfterClass
	public void destroy() throws InterruptedException {
		ganjex.destroy();
	}

}
