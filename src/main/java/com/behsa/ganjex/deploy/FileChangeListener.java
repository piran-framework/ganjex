package com.behsa.ganjex.deploy;

import java.io.File;

/**
 * this listener is responsible to handle the change in a directory which {@link JarWatcher} watch
 * it take action when a file modified or removed. modified file include adding new files
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public interface FileChangeListener {

	/**
	 * listener which executed when a file added into or modified in a specific path
	 *
	 * @param file the new file
	 */
	void fileAdd(File file);

	/**
	 * listener which executed when a file removed in a specific path
	 *
	 * @param file file which be removed
	 */
	void fileRemoved(File file);
}
