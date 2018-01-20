package com.behsa.ganjex.e2e.hooksExecution;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.e2e.GanjexExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.e2e.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Esa Hekmatizadeh
 */
@ExtendWith(GanjexExtension.class)
class HooksExecution {
	@Test
	void testJustOneObjectWithDifferentHooks() throws IOException, InterruptedException,
					ClassNotFoundException,
					NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		deployLib(TEST_PATH + "hooksExecution/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		invokeStaticMethod("com.behsa.LibWithDifferentHooks", "clean", Void.class,
						new Class[0]);
		deployService(TEST_PATH + "hooksExecution/simpleService/", "simple-service");
		Thread.sleep(2000);
		int runTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getRunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, runTime);
	}

	@Test
	void testEveryHooksRunOnce() throws IOException, InterruptedException,
					ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		deployLib(TEST_PATH + "hooksExecution/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
		invokeStaticMethod("com.behsa.LibWithDifferentHooks", "clean", Void.class,
						new Class[0]);
		int shutdownHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getShutdownHookRunTime", Integer.class, new Class<?>[0]);
		assertEquals(0, shutdownHookRunTime);
		deployService(TEST_PATH + "hooksExecution/simpleService/", "simple-service");
		Thread.sleep(3000);
		int startHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHookRunTime", Integer.class, new Class<?>[0]);
		int startHook2RunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getStartHook2RunTime", Integer.class, new Class<?>[0]);
		assertEquals(1, startHookRunTime);
		assertEquals(1, startHook2RunTime);
		unDeployService("simple-service");
		Thread.sleep(3000);
		shutdownHookRunTime = invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getShutdownHookRunTime", Integer.class, new Class<?>[0]);
		assertNull(Bootstrap.lifecycleManagement().findContext("simple-service"));
		assertEquals(1, shutdownHookRunTime);
	}
}
