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

package com.behsa.ganjex.container;

import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import com.behsa.ganjex.api.StartupHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * {@link HookLoader} is a class responsible to load all hooks in the hook objects provided in
 * the {@link com.behsa.ganjex.api.GanjexConfiguration} and register all of them into
 * {@link com.behsa.ganjex.lifecycle.LifecycleManagement} instance
 *
 * @author hekmatof
 * @since 1.0
 */
public final class HookLoader {
	private final Logger log = LoggerFactory.getLogger(HookLoader.class);
	/**
	 * container configuration and state object also useful to get <code>lifecycleManagement</code>
	 * instance
	 */
	private final GanjexApplication app;

	/**
	 * construct a new <code>HookLoader</code> with the ganjex container instance passed to it
	 *
	 * @param app ganjex container state object
	 */
	public HookLoader(GanjexApplication app) {
		this.app = app;
	}

	/**
	 * Load all methods in the hook objects and register them in the
	 * {@link com.behsa.ganjex.lifecycle.LifecycleManagement} instance of the container
	 */
	public void loadHooks() {
		Stream.of(app.config().getHooks()).forEach(this::registerHooksOfObject);
	}

	private void registerHooksOfObject(Object object) {
		Stream.of(object.getClass().getMethods())
						.filter(method -> method.isAnnotationPresent(StartupHook.class))
						.forEach(method -> app.lifecycleManagement().registerStartupHook(
										new com.behsa.ganjex.lifecycle.StartupHook(createHook(method, object),
														method.getAnnotation(StartupHook.class).priority())));
		Stream.of(object.getClass().getMethods())
						.filter(method -> method.isAnnotationPresent(ShutdownHook.class))
						.forEach(method -> app.lifecycleManagement().registerShutdownHook(
										new com.behsa.ganjex.lifecycle.ShutdownHook(createHook(method, object),
														method.getAnnotation(ShutdownHook.class).priority())));
	}

	private Consumer<ServiceContext> createHook(Method m, Object o) {
		return (ServiceContext s) -> {
			try {
				m.invoke(o, s);
			} catch (IllegalAccessException e) {
				log.error("could not execute hook {} of class {} for service {} version{} because the " +
												"hook method is not accessible", m.getName(),
								m.getDeclaringClass().getName(), s.getName(), s.getVersion());
			} catch (InvocationTargetException e) {
				log.error("could not execute hook {} of class {} for service {} version {},cause:",
								m.getName(), m.getDeclaringClass().getName(), s.getName(), s.getVersion(),
								e.getTargetException());
			}
		};
	}
}
