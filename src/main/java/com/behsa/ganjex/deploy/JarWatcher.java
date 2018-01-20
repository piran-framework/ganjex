package com.behsa.ganjex.deploy;


import com.behsa.ganjex.util.JarFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.behsa.ganjex.util.Executors.scheduledExecutor;

/**
 * The <b>JarWatcher</b> watch the given directory and notify the given listener when change in
 * jar files in that directory detected
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public final class JarWatcher {
	private static final Logger log = LoggerFactory.getLogger(JarWatcher.class);
	/**
	 * Currently deployed files
	 */
	private final Map<String, JarInfo> currentStatus = new HashMap<>();
	/**
	 * Directory to watch for service jar files
	 */
	private final File watchDir;
	/**
	 * Listener to be notified of changes
	 */
	private final FileChangeListener listener;

	private ScheduledFuture<?> scheduledFuture;

	/**
	 * create a new <code>JarWatcher</code>
	 *
	 * @param watchDir the directory to watch
	 * @param listener the listener to notify when changed detected
	 */
	public JarWatcher(File watchDir, FileChangeListener listener) {
		this.watchDir = watchDir;
		this.listener = listener;
		scheduledFuture = scheduledExecutor().scheduleWithFixedDelay(this::check
						, 1, 1, TimeUnit.SECONDS);

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
		for (Iterator<Map.Entry<String, JarInfo>> i =
				 currentStatus.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<String, JarInfo> entry = i.next();
			JarInfo info = entry.getValue();
			int check = info.check();
			if (check == 1) {
				listener.fileModified(info.jar);
			} else if (check == -1) {
				listener.fileRemoved(info.jar);
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
		JarInfo info = currentStatus.get(jarfile.getAbsolutePath());
		if (info == null) {
			info = new JarInfo(jarfile);
			info.setLastState(-1); //assume file is non existent
			currentStatus.put(jarfile.getAbsolutePath(), info);
		}
	}

	public void destroy() {
		scheduledFuture.cancel(true);
	}

	/**
	 * the information of the jar files. check for changes in the status of the files and
	 * find out of the removed filed
	 */
	private static class JarInfo {
		private final File jar;

		private long lastChecked = 0;

		private long lastState = 0;

		private JarInfo(File jar) {
			this.jar = jar;
			this.lastChecked = jar.lastModified();
			if (!jar.exists())
				lastState = -1;
		}

		private boolean modified() {
			return jar.exists() && jar.lastModified() > lastChecked;
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
			//file unchanged by default
			int result = 0;

			if (modified()) {
				//file has changed - timestamp
				result = 1;
				lastState = result;
			} else if ((!exists()) && (!(lastState == -1))) {
				//file was removed
				result = -1;
				lastState = result;
			} else if ((lastState == -1) && exists()) {
				//file was added
				result = 1;
				lastState = result;
			}
			this.lastChecked = System.currentTimeMillis();
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
