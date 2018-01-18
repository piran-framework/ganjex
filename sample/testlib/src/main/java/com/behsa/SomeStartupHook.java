package com.behsa;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.StartupHook;

/**
 * @author Esa Hekmatizadeh
 */
public class SomeStartupHook {
	@StartupHook(priority = 120)
	public void saySomthing(ServiceContext context) {
		System.out.println("in the startup hook with priority 120: service name is: "
						+ context.getName());
	}

	@StartupHook(priority = 110)
	public void anotherHook(ServiceContext context) {
		System.out.println("in the startup hook with priority 110: service name is: "
						+ context.getName());
	}
}
