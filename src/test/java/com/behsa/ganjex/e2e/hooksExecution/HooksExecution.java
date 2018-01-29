package com.behsa.ganjex.e2e.hooksExecution;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.e2e.TestUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.e2e.TestUtil.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Esa Hekmatizadeh
 */
@Test(sequential = true)
public class HooksExecution {

	@Test
	public void testJustOneObjectWithDifferentHooks() throws IOException, InterruptedException,
					ClassNotFoundException,
					NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		TestUtil.clean();
		deployLib(TEST_PATH + "hooksExecution/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		invokeStaticMethod("com.behsa.LibWithDifferentHooks", "clean", Void.class,
						new Class[0]);
		deployService(TEST_PATH + "hooksExecution/simpleService/", "simple-service");
		Thread.sleep(TIMEOUT);
		int runTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getRunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, runTime);
		Bootstrap.destroy();
	}

	@Test
	public void testEveryHooksRunOnce() throws IOException, InterruptedException,
					ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		TestUtil.clean();
		deployLib(TEST_PATH + "hooksExecution/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		invokeStaticMethod("com.behsa.LibWithDifferentHooks", "clean", Void.class,
						new Class[0]);
		int shutdownHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getShutdownHookRunTime", Integer.class, new Class<?>[0]);
		assertEquals(0, shutdownHookRunTime);
		deployService(TEST_PATH + "hooksExecution/simpleService/", "simple-service");
		Thread.sleep(TIMEOUT);
		int startHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHookRunTime", Integer.class, new Class<?>[0]);
		int startHook2RunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHook2RunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, startHookRunTime);
		assertEquals(1, startHook2RunTime);
		unDeployService("simple-service");
		Thread.sleep(TIMEOUT);
		shutdownHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getShutdownHookRunTime", Integer.class, new Class<?>[0]);
		assertNull(Bootstrap.lifecycleManagement().findContext("simple-service"));
		assertEquals(1, shutdownHookRunTime);
		Bootstrap.destroy();
	}
}
