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

package com.behsacorp.ganjex.watch;

import java.io.File;

/**
 * This listener is responsible to handle the change in a directory which {@link JarWatcher} watch
 * it take action when a file added or removed. fileAdd include modified an existing file
 *
 * @author hekmatof
 * @since 1.0
 */
interface FileChangeListener {

  /**
   * listener which executed when a file added into or modified in the specific path
   *
   * @param file the new file
   */
  void fileAdd(File file);

  /**
   * listener which executed when a file removed in the specific path
   *
   * @param file file which be removed
   */
  void fileRemoved(File file);
}
