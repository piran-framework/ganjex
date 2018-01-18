package com.behsa.ganjex.e2e.hooksRun;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.e2e.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.e2e.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
class HooksRun {
	@BeforeEach
	void clean() throws IOException {
		TestUtil.clean();
	}

	@Test
	void testJustOneObjectWithDifferentHooks() throws IOException, InterruptedException,
					ClassNotFoundException,
					NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		deployLib(TEST_PATH + "hooksRun/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		deployService(TEST_PATH + "hooksRun/simpleService/", "simple-service");
		Thread.sleep(2000);
		int runTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getRunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, runTime);
	}

	@Test
	void testEveryStartHooksRunOnce() throws IOException, InterruptedException,
					ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		deployLib(TEST_PATH + "hooksRun/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		deployService(TEST_PATH + "hooksRun/simpleService/", "simple-service");
		Thread.sleep(2000);
		int startHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHookRunTime", Integer.class, new Class<?>[0]);
		int startHook2RunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHook2RunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, startHookRunTime);
		assertEquals(1, startHook2RunTime);
		unDeployService("simple-service");
		Thread.sleep(2000);
		int shutdownHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getShutdownHookRunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, shutdownHookRunTime);
	}


}
