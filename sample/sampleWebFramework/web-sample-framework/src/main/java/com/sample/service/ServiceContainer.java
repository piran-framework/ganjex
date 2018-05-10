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

package com.sample.service;

import com.behsacorp.ganjex.api.ServiceContext;
import com.sample.api.Action;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author hekmatof
 */
@GanjexHook
public class ServiceContainer {
	private static final Logger log = LoggerFactory.getLogger(ServiceContainer.class);
	private final Map<String, Map<String, Function<Map<String, Object>, Map<String, Object>>>>
					actions = new HashMap<>();
	private final Map<Class<?>, Object> instances = new HashMap<>();

	@StartupHook
	public void add(ServiceContext context) {
		Reflections.log = LoggerFactory.getLogger(Reflections.class);
		Reflections reflections = new Reflections(new MethodAnnotationsScanner(),
						context.getClassLoader());
		Set<Method> actionMethods = reflections.getMethodsAnnotatedWith(Action.class);
		Map<String, Function<Map<String, Object>, Map<String, Object>>> actionsOfTheModule = new
						HashMap<>();
		actionMethods.forEach((Method m) -> {
			if (!instances.containsKey(m.getDeclaringClass())) {
				try {
					instances.put(m.getDeclaringClass(), m.getDeclaringClass().getConstructor().newInstance());
				} catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
								InvocationTargetException e) {
					log.error("error in initializing class {}", m.getDeclaringClass(), e);
				}
			}
			Action action = m.getAnnotation(Action.class);
			actionsOfTheModule.put(action.value(), input -> {
				try {
					return (Map<String, Object>) m.invoke(instances.get(m.getDeclaringClass()), input);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error("error invoking method {} of class {}", m, m.getDeclaringClass(), e);
					return null;
				}
			});
			log.info("action {} of module {} added", action.value(), context.getName());
		});
		actions.put(context.getName(), actionsOfTheModule);
	}

  @ShutdownHook
	public void remove(ServiceContext context) {
		actions.remove(context.getName());
	}

	public Function<Map<String, Object>, Map<String, Object>> get(String moduleName,
																																String actionName) {
		Map<String, Function<Map<String, Object>, Map<String, Object>>> moduleMap =
						actions.get(moduleName);
		if (Objects.isNull(moduleMap))
			throw new IllegalStateException();
		Function<Map<String, Object>, Map<String, Object>> function = moduleMap.get(actionName);
		if (Objects.isNull(function))
			throw new IllegalStateException();
		return function;
	}
}
