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

package com.behsa.ganjex.integration.multipleVersion;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.integration.TestUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.integration.TestUtil.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Esa Hekmatizadeh
 */
@Test(sequential = true)
public class MultiVersionIT {
	@Test
	public void testReplace() throws IOException, InterruptedException, ClassNotFoundException,
					NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		TestUtil.clean();
		deployLib(TEST_PATH + "multipleVersion/callServiceDirectlyLibrary", "call-direct-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		deployService(TEST_PATH + "multipleVersion/versionService/", "version-service");
		Thread.sleep(TIMEOUT);
		int version1 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 1);
		assertEquals(version1, 1);
		deployService(TEST_PATH + "multipleVersion/versionService2/", "version-service");
		Thread.sleep(TIMEOUT);
		long count = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"serviceCount", Long.class, new Class<?>[]{String.class},
						"version-service");
		assertEquals(count, 1L);
		int version2 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 2);
		assertEquals(version2, 2);
	}

	@Test
	public void testTwoVersion() throws IOException, InterruptedException, ClassNotFoundException,
					NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		TestUtil.clean();
		deployLib(TEST_PATH + "multipleVersion/callServiceDirectlyLibrary", "call-direct-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		deployService(TEST_PATH + "multipleVersion/versionService/", "version-service");
		Thread.sleep(TIMEOUT);
		int version1 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 1);
		assertEquals(version1, 1);
		deployService(TEST_PATH + "multipleVersion/versionService2/", "version-service2");
		Thread.sleep(TIMEOUT);
		int version2 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 2);
		assertEquals(2, version2);
		version1 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 1);
		assertEquals(1, version1);
	}

}
