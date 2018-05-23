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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The <code>JarWatcher</code> object watch the given directory and notify the given listener when
 * change in jar files in that directory detected
 *
 * @author hekmatof
 * @since 1.0
 */
public final class JarWatcher {
  private static final Logger log = LoggerFactory.getLogger(JarWatcher.class);
  /**
   * Currently deployed files
   */
  private final Map<String, FileInfo> currentStatus = new HashMap<>();
  /**
   * Directory to watch for service jar files
   */
  private final File watchDir;
  /**
   * Listener to be notified of changes
   */
  private final FileChangeListener listener;
  private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);

  private final ScheduledFuture<?> scheduledFuture;

  /**
   * create a new <code>JarWatcher</code>
   *
   * @param watchDir the directory to watch
   * @param listener the listener to notify when changed detected
   */
  public JarWatcher(File watchDir, FileChangeListener listener, long watcherDelay) {
    this.watchDir = watchDir;
    this.listener = listener;
    scheduledFuture = executor.scheduleWithFixedDelay(this::check
      , 0, watcherDelay, TimeUnit.SECONDS);

  }

  /**
   * check for modification and send notification to listener
   */
  private void check() {
    log.trace("checking {} directory to find changes", watchDir.getPath());
    File[] list = watchDir.listFiles(new JarFilter());
    if (list == null)
      list = new File[0];
    Arrays.stream(list).forEach(f -> {
        if (!f.exists())
          log.warn("listed file does not exist: {}", f);
        addJarInfo(f);
      }
    );

    // Check all the status codes and notify the listener
    for (Iterator<Map.Entry<String, FileInfo>> i =
         currentStatus.entrySet().iterator(); i.hasNext(); ) {
      Map.Entry<String, FileInfo> entry = i.next();
      FileInfo info = entry.getValue();
      int check = info.check();
      if (check == 1) {
        listener.fileAdd(info.getFile());
      } else if (check == -1) {
        listener.fileRemoved(info.getFile());
        //no need to keep in memory
        i.remove();
      }
    }
  }

  /**
   * add jar to the watcher state
   *
   * @param jarfile The JAR to add
   */
  private void addJarInfo(File jarfile) {
    if (!currentStatus.containsKey(jarfile.getAbsolutePath())) {
      FileInfo info = new FileInfo(jarfile);
      info.setLastState(-1); //assume file is non existent
      currentStatus.put(jarfile.getAbsolutePath(), info);
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
