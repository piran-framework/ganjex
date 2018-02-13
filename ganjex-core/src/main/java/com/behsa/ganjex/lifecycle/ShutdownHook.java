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
 * {@link ShutdownHook} annotation
 *
 * @author hekmatof
 * @since 1.0
 */
public class ShutdownHook implements Comparable<ShutdownHook> {
	private final Consumer<ServiceContext> hook;
	private final Integer priority;

	public ShutdownHook(Consumer<ServiceContext> hook, Integer priority) {
		this.hook = hook;
		this.priority = priority == null ? 100 : priority;
	}

	Consumer<ServiceContext> hook() {
		return hook;
	}

	@Override
	public int compareTo(ShutdownHook o) {
		return priority.compareTo(o.priority);
	}

}
