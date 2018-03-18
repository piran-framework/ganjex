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

package com.behsa.ganjex.integration.multipleVersion;

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
public class MultiVersionIT {
	@Test
	public void testReplace() throws IOException, InterruptedException, ClassNotFoundException,
					NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		clean();
		CallServiceDirectlyLibrary.clear();
		Ganjex ganjex = Ganjex.run(new GanjexConfiguration.Builder()
						.libPath(libPath)
						.servicePath(servicePath)
						.watcherDelay(1)
						.hooks(new CallServiceDirectlyLibrary())
						.build());
		waitToBootstrap();
		deployService(TEST_PATH + "multipleVersion/versionService/", "version-service");
		Thread.sleep(TIMEOUT);
		int version1 = CallServiceDirectlyLibrary.getVersion("version-service", 1);
		assertEquals(version1, 1);
		deployService(TEST_PATH + "multipleVersion/versionService2/", "version-service");
		Thread.sleep(TIMEOUT);
		long count = CallServiceDirectlyLibrary.serviceCount("version-service");
		assertEquals(count, 1L);
		int version2 = CallServiceDirectlyLibrary.getVersion("version-service", 2);
		assertEquals(version2, 2);
		ganjex.destroy();
	}

	@Test
	public void testTwoVersion() throws IOException, InterruptedException, ClassNotFoundException,
					NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		clean();
		CallServiceDirectlyLibrary.clear();
		Ganjex ganjex = Ganjex.run(new GanjexConfiguration.Builder()
						.libPath(libPath)
						.servicePath(servicePath)
						.watcherDelay(1)
						.hooks(new CallServiceDirectlyLibrary())
						.build());
		waitToBootstrap();
		deployService(TEST_PATH + "multipleVersion/versionService/", "version-service");
		Thread.sleep(TIMEOUT);
		int version1 = CallServiceDirectlyLibrary.getVersion("version-service", 1);
		assertEquals(version1, 1);
		deployService(TEST_PATH + "multipleVersion/versionService2/", "version-service2");
		Thread.sleep(TIMEOUT);
		int version2 = CallServiceDirectlyLibrary.getVersion("version-service", 2);
		assertEquals(2, version2);
		version1 = CallServiceDirectlyLibrary.getVersion("version-service", 1);
		assertEquals(1, version1);
		ganjex.destroy();
	}

}
