package com.behsa.ganjex.e2e.multipleVersion;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.e2e.TestUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static com.behsa.ganjex.e2e.TestUtil.*;
import static org.testng.Assert.assertEquals;

/**
 * @author Esa Hekmatizadeh
 */
@Test(sequential = true)
public class MultiVersion {

	@Test(threadPoolSize = 10)
	public void testTwoVersion() throws IOException, InterruptedException, ClassNotFoundException,
					NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		TestUtil.clean();
		deployLib(TEST_PATH + "multipleVersion/callServiceDirectlyLibrary", "call-direct-lib");
		Bootstrap.main(new String[]{TEST_CONFIG_PATH});
		waitToBootstrap();
//		new Thread(() -> {
//			try {
				deployService(TEST_PATH + "multipleVersion/versionService/", "version-service");
//			} catch (IOException | InterruptedException e) {
//				e.printStackTrace();
//			}
//		});
		Thread.sleep(TIMEOUT);
		int version1 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 1);
		assertEquals(1, version1);
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

	@Test(threadPoolSize = 10)
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
		assertEquals( version1,1);
		new Thread(() -> {
			try {
				deployService(TEST_PATH + "multipleVersion/versionService2/", "version-service");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		System.out.println("second deployment done");
		Thread.sleep(TIMEOUT);
		long count = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"serviceCount", Long.class, new Class<?>[]{String.class},
						"version-service");
		assertEquals(count,1L);
		int version2 = invokeStaticMethod("com.behsa.CallServiceDirectlyLibrary",
						"getVersion", Integer.class, new Class<?>[]{String.class, Integer.class},
						"version-service", 2);
		assertEquals( version2,2);
	}
}
