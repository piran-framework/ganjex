package com.behsa.ganjex.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * filter class to find jar files
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class JarFilter implements FilenameFilter {
	@Override
	public boolean accept(File dir, String name) {
		return name != null && name.endsWith(".jar");
	}
}
