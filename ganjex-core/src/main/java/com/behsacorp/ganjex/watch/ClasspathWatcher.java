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
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The <code>ClasspathWatcher</code> object watch the given directory and notify the
 * given listener when change in class files in that directory detected
 *
 * @author omidp
 * @since 1.0
 */
public final class ClasspathWatcher implements Watcher {
	private static final Logger log = LoggerFactory.getLogger(ClasspathWatcher.class);
	/**
	 * Currently deployed files
	 */
	private final Map<String, JarInfo> currentStatus = new HashMap<>();
	
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
	 * @param watchDir
	 *            the directory to watch
	 * @param listener
	 *            the listener to notify when changed detected
	 * @param set 
	 */
	public ClasspathWatcher(FileChangeListener listener, long watcherDelay, Set<String> classPaths) {
		this.listener = listener;
		 scheduledFuture = executor.scheduleWithFixedDelay(this::check
		 , 0, watcherDelay, TimeUnit.SECONDS);		
		 this.classPaths = classPaths == null ? new HashSet<>() : classPaths;
	}

	/**
	 * check for modification and send notification to listener
	 */
	public void check() {
		classPaths.forEach(dir->{
			File directoryPath = new File(dir);
			if(directoryPath.exists())
			{
				log.trace("checking {} directories to find changes", dir);
				Collection<File> listFiles = FileUtils.listFiles(new File(dir), new String[] { "class" }, true);
				if (listFiles != null && listFiles.isEmpty() == false) {
					listFiles.forEach(f -> {
						if (f.exists())
							addJarInfo(f);
					});
					
					// Check all the status codes and notify the listener
					for (Iterator<Map.Entry<String, JarInfo>> i = currentStatus.entrySet().iterator(); i.hasNext();) {
						Map.Entry<String, JarInfo> entry = i.next();
						JarInfo info = entry.getValue();
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
	 * add jar to the watcher state
	 *
	 * @param jarfile
	 *            The JAR to add
	 */
	private void addJarInfo(File jarfile) {
		JarInfo info = currentStatus.get(jarfile.getAbsolutePath());
		if (info == null) {
			info = new JarInfo(jarfile);
			info.setLastState(-1); // assume file is non existent
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

	/**
	 * the information of the jar files. check for changes in the status of the
	 * files and find out of the removed filed
	 */
	private static class JarInfo {
		private final File jar;

		private long lastModified = 0;
		private long lastState = 0;

		private JarInfo(File jar) {
			this.jar = jar;
			this.lastModified = jar.lastModified();
			if (!jar.exists())
				lastState = -1;
		}

		private boolean modified() {
			return jar.exists() && (jar.lastModified() > lastModified);
		}

		private boolean exists() {
			return jar.exists();
		}

		/**
		 * Returns 1 if the file has been added/modified, 0 if the file is
		 * unchanged and -1 if the file has been removed
		 *
		 * @return int 1=file added; 0=unchanged; -1=file removed
		 */
		private int check() {
			// file unchanged by default
			int result = 0;
			if (modified()) {
				// file has changed - timestamp
				result = 1;
				lastState = result;
				this.lastModified = jar.lastModified();
			} else if ((!exists()) && (!(lastState == -1))) {
				// file was removed
				result = -1;
				lastState = result;
			} else if ((lastState == -1) && exists()) {
				// file was added
				result = 1;
				lastState = result;
			}
			return result;
		}

		@Override
		public int hashCode() {
			return jar.getAbsolutePath().hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof JarInfo) {
				JarInfo jo = (JarInfo) other;
				return jo.jar.equals(jar);
			} else {
				return false;
			}
		}

		private void setLastState(int lastState) {
			this.lastState = lastState;
		}

	}

}
