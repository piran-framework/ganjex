package com.behsa.ganjex.e2e;

import com.behsa.ganjex.bootstrap.Bootstrap;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Esa Hekmatizadeh
 */
public class GanjexExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
	@Override
	public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
		Bootstrap.destroy();
	}

	@Override
	public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
		TestUtil.clean();
	}
}
