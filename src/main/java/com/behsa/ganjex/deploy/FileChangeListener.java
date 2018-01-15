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

	void fileModified(File f);

	void fileRemoved(File f);
}
