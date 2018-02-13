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

package com.behsa.ganjex.api;

import java.lang.annotation.*;

/**
 * Shutdown hook annotation, it is used by the framework to register their hook
 * which should be executed right after a service removed.
 * <p>
 * Note that if a library changed all of the {@link ShutdownHook} for all of the services
 * invoked, libraries reloaded and after that all of the {@link StartupHook} for all the services
 * invoked again.
 * <p>
 * every method annotated with {@link ShutdownHook} should be surrounded with a class which
 * contains a default constructor and accept just one parameter of type {@link ServiceContext},
 * this parameter indicates some information about the service, also containing service classLoader
 * which hooks can use to find elements in the services
 *
 * @author hekmatof
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShutdownHook {
	/**
	 * the priority of this hook, low number executed with high priority, it's better to not use 0
	 * and any other low number if it's not very necessary
	 *
	 * @return the priority of this hook
	 */
	int priority() default 100;
}
