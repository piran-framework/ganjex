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

package com.sample.service;

import com.behsa.ganjex.api.ServiceContext;
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
@Service
public class ServiceContainer {
	private static final Logger log = LoggerFactory.getLogger(ServiceContainer.class);
	private final Map<String, Map<String, Function<Map<String, Object>, Map<String, Object>>>>
					actions = new HashMap<>();
	private final Map<Class<?>, Object> instances = new HashMap<>();

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
