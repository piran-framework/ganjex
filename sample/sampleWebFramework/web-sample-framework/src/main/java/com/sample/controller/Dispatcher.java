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

package com.sample.controller;

import com.sample.service.ServiceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author hekmatof
 */
@RestController
public class Dispatcher {
	private final ServiceContainer container;

	@Autowired
	public Dispatcher(ServiceContainer container) {
		this.container = container;
	}

	@PostMapping("{moduleName}/{action}")
	public ResponseEntity<?> dispatch(@PathVariable String moduleName,
																		@PathVariable String action,
																		@RequestBody Map<String, Object> object) {
		try {
			return ResponseEntity.ok(container.get(moduleName, action).apply(object));
		} catch (IllegalStateException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
