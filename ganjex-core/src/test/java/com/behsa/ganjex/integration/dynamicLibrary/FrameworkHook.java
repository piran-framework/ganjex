/*
 * Copyright (c) 2018 Behsa Corporation.
 *
 *   This file is part of Ganjex.
 *
 *    Ganjex is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Ganjex is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Ganjex.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.behsa.ganjex.integration.dynamicLibrary;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.StartupHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hekmatof
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
