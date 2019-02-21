/*
 * Copyright (c) 2018 Isa Hekmatizadeh.
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

package com.piran.ganjex.watch;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter class implemented {@link FilenameFilter} to indicate jar files
 *
 * @author hekmatof
 * @since 1.0
 */
public class JarFilter implements FilenameFilter {
  @Override
  public boolean accept(File dir, String name) {
    return name != null && name.endsWith(".jar");
  }
}
