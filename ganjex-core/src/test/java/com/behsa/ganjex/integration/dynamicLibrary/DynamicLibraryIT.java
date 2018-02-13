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

package com.behsa.ganjex.integration.dynamicLibrary;

import com.behsa.ganjex.api.Ganjex;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.integration.TestUtil.*;

/**
 * @author hekmatof
 */
@Test(sequential = true)
public class DynamicLibraryIT {
private Ganjex ganjex;
	@Test
	public void testDynamicLibrary() throws IOException, InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		clean();
		ganjex = Ganjex.run(config);
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
