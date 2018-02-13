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

package com.behsa;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.StartupHook;

/**
 * @author hekmatof
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
