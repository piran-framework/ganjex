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
 * the information of the files. check for changes in the status of the files and
 * find out of the removed filed
 *
 * @author hekmatof
 */
public class FileInfo {
  private final File file;

  private long lastModified = 0;
  private long lastState = 0;

  FileInfo(File file) {
    this.file = file;
    this.lastModified = file.lastModified();
    if (!file.exists())
      lastState = -1;
  }

  private boolean modified() {
    return file.exists() && (file.lastModified() > lastModified);
  }

  private boolean exists() {
    return file.exists();
  }

  /**
   * Returns 1 if the file has been added/modified, 0 if the file is
   * unchanged and -1 if the file has been removed
   *
   * @return int 1=file added; 0=unchanged; -1=file removed
   */
  int check() {
    //file unchanged by default
    int result = 0;
    if (modified()) {
      //file has changed - timestamp
      result = 1;
      lastState = result;
      this.lastModified = file.lastModified();
    } else if ((!exists()) && (!(lastState == -1))) {
      //file was removed
      result = -1;
      lastState = result;
    } else if ((lastState == -1) && exists()) {
      //file was added
      result = 1;
      lastState = result;
    }
    return result;
  }


  @Override
  public int hashCode() {
    return file.getAbsolutePath().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof FileInfo) {
      FileInfo jo = (FileInfo) other;
      return jo.file.equals(file);
    } else {
      return false;
    }
  }

  void setLastState(int lastState) {
    this.lastState = lastState;
  }


  public File getFile() {
    return file;
  }
}
