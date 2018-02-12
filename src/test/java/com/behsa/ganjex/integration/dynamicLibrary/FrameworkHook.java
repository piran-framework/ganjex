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

import com.behsa.ganjex.api.StartupHook;
import com.behsa.ganjex.api.ServiceContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Esa Hekmatizadeh
 */
public class FrameworkHook {
	private static Object actionObject;
	private static Class<?> actionClass;

	public static String invokeMethodOnService() throws NoSuchMethodException,
					IllegalAccessException, InvocationTargetException {
		Method action = actionClass.getMethod("action");
		return (String) action.invoke(actionObject);
	}

	@StartupHook
	public void startup(ServiceContext context) throws NoSuchMethodException,
					IllegalAccessException, InvocationTargetException, InstantiationException {
		try {
			actionClass = context.getClassLoader().loadClass("com.behsa.SomeService");
			actionObject = actionClass.getConstructor((Class<?>[]) null).newInstance((Object[]) null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
