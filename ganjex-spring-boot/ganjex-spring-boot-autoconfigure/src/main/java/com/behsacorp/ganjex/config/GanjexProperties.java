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

package com.behsacorp.ganjex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hekmatof
 * @since 1.0
 */
@ConfigurationProperties("ganjex")
public class GanjexProperties {
	private String servicePath = "service";
	private String libPath = "lib";
	private int watchDelay = 10;

	public String getServicePath() {
		return servicePath;
	}

	public GanjexProperties setServicePath(String servicePath) {
		this.servicePath = servicePath;
		return this;
	}

	public String getLibPath() {
		return libPath;
	}

	public GanjexProperties setLibPath(String libPath) {
		this.libPath = libPath;
		return this;
	}

	public int getWatchDelay() {
		return watchDelay;
	}

	public GanjexProperties setWatchDelay(int watchDelay) {
		this.watchDelay = watchDelay;
		return this;
	}
}
