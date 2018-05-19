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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The <code>ClasspathWatcher</code> object watches the classpath directory and notifies the
 * given listener as soon as any changes in class files in that directory are detected.
 *
 * @author omidp
 * @since 1.0
 */
public final class ClasspathWatcher {
  private static final Logger log = LoggerFactory.getLogger(ClasspathWatcher.class);
  /**
   * Currently classpath files
   */
  private final Map<String, FileInfo> currentStatus = new HashMap<>();

  /**
   * Listener to be notified of changes
   */
  private final FileChangeListener listener;
  private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

  private final ScheduledFuture<?> scheduledFuture;

  private Set<String> classPaths;

  /**
   * create a new <code>JarWatcher</code>
   *
   * @param classPaths   list of directories to watch
   * @param listener     the listener to notify when changed detected
   * @param watcherDelay delay between checks in second
   */
  public ClasspathWatcher(Set<String> classPaths, FileChangeListener listener,
                          long watcherDelay) {
    this.listener = listener;
    scheduledFuture = executor.scheduleWithFixedDelay(this::check
        , 0, watcherDelay, TimeUnit.SECONDS);
    this.classPaths = classPaths == null ? new HashSet<>() : classPaths;
  }

  /**
   * check for modification and send notification to listener
   */
  private void check() {
    classPaths.forEach(dir -> {
      File directoryPath = new File(dir);
      if (directoryPath.exists()) {
        log.trace("checking {} directories to find changes", dir);
        Collection<File> listFiles = FileUtils.listFiles(new File(dir), new String[]{"class"}, true);
        if (!listFiles.isEmpty()) {
          listFiles.forEach(f -> {
            if (f.exists())
              addFileInfo(f);
          });

          // Check all the status codes and notify the listener
          for (Iterator<Map.Entry<String, FileInfo>> i = currentStatus.entrySet().iterator();
               i.hasNext(); ) {
            Map.Entry<String, FileInfo> entry = i.next();
            FileInfo info = entry.getValue();
            int check = info.check();
            if (check == 1) {
              listener.fileAdd(directoryPath);
            } else if (check == -1) {
              listener.fileRemoved(directoryPath);
              // no need to keep in memory
              i.remove();
            }
          }
        }
      }
    });
  }

  /**
   * add file to the watcher state
   *
   * @param file The file to add
   */
  private void addFileInfo(File file) {
    FileInfo info = currentStatus.get(file.getAbsolutePath());
    if (info == null) {
      info = new FileInfo(file);
      info.setLastState(-1); // assume file is non existent
      currentStatus.put(file.getAbsolutePath(), info);
    }
  }

  /**
   * useful for testing, interrupt watcher thread
   */
  public void destroy() {
    scheduledFuture.cancel(true);
    try {
      executor.awaitTermination(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    }
    if (!executor.isTerminated())
      executor.shutdownNow();
    currentStatus.clear();
  }

}
