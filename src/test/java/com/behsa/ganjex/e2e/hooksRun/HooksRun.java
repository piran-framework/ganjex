package com.behsa.ganjex.e2e.hooksRun;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.e2e.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Esa Hekmatizadeh
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
		TestUtil.deployLib(TestUtil.TEST_PATH + "hooksRun/libWithDifferentHooks/", "simple-lib");
		Bootstrap.main(new String[]{TestUtil.TEST_CONFIG_PATH});
		TestUtil.waitToBootstrap();
		TestUtil.deployService(TestUtil.TEST_PATH + "hooksRun/simpleService/", "simple-service");
		Thread.sleep(2000);
		int runTime = TestUtil.invokeStaticMethod("com.behsa.LibWithDifferentHooks",
						"getRunTime", Integer.class, new Class<?>[0]);
		Assertions.assertEquals(1, runTime);
	}
}
