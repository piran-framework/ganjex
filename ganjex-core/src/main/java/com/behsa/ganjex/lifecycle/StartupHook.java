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

package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;

import java.util.function.Consumer;

/**
 * inner representation of the methods in the framework which annotated with the
 * {@link StartupHook} annotation
 *
 * @author Esa Hekmatizadeh
 * @since 1.0
 */
public class StartupHook implements Comparable<StartupHook> {
	private final Consumer<ServiceContext> hook;
	private Integer priority = 100;

	public StartupHook(Consumer<ServiceContext> hook) {
		this.hook = hook;
	}

	Consumer<ServiceContext> hook() {
		return hook;
	}

	@Override
	public int compareTo(StartupHook o) {
		return priority.compareTo(o.priority);
	}
}
