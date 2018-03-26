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

package com.behsacorp.ganjex.api;

import java.lang.annotation.*;

/**
 * Startup hook annotation, it is used by the framework to register their hook
 * which should be executed right after a new service loaded.
 * <p>
 * Note that if a library changed all of the {@link ShutdownHook} for all of the services
 * invoked, libraries reloaded and after that all of the {@link StartupHook} for all the services
 * invoked again.
 * <p>
 * every method annotated with {@link StartupHook} should be surrounded with a class which
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
public @interface StartupHook {
	/**
	 * the priority of this hook, low number executed with high priority, it's better to not use 0
	 * and any other low number if it's not very necessary
	 *
	 * @return the priority of this hook
	 */
	int priority() default 100;
}
