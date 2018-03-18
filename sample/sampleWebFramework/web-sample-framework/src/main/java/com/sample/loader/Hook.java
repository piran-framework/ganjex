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

package com.sample.loader;

import com.behsa.ganjex.GanjexHook;
import com.behsa.ganjex.api.ServiceContext;
import com.behsa.ganjex.api.ShutdownHook;
import com.behsa.ganjex.api.StartupHook;
import com.sample.service.ServiceContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hekmatof
 */
@GanjexHook
public class Hook {
	@Autowired
	private ServiceContainer container;

	@StartupHook
	public void startService(ServiceContext context) {
		container.add(context);
	}

	@ShutdownHook
	public void shutdownService(ServiceContext context) {
		container.remove(context);
	}
}
