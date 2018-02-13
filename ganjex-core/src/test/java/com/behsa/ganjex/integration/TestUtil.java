/*
 * Copyright 2018 Behsa Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.behsa.ganjex.integration;

import com.behsa.ganjex.api.Ganjex;
import com.behsa.ganjex.api.GanjexConfiguration;
import com.behsa.ganjex.watch.JarFilter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Test utilities for ganjex
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class TestUtil {
	/**
	 * directory where all the integration tests placed relative to the root of the project
	 */
	public static final String TEST_PATH = "src/test/java/com/behsa/ganjex/integration/";

	public static final long TIMEOUT = 2000L;
	public final static GanjexConfiguration config;
	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	static {
		config = new GanjexConfiguration.Builder()
						.basePackage("com.behsa")
						.libPath("test-dist/libs/")
						.servicePath("test-dist/services/")
						.watcherDelay(1)
						.build();
	}

	/**
	 * delete the tmp and services directory and libraries directory and recreate them
	 *
	 * @throws IOException if it cannot delete or make a directory
	 */
	public static void clean() throws IOException {
		File servicePath = new File(config.getServicePath());
		File libPath = new File(config.getLibPath());
		if (servicePath.exists())
			deleteAllFilesInDirectory(servicePath, new String[]{"jar"});
		if (libPath.exists())
			deleteAllFilesInDirectory(libPath, new String[]{"jar"});
		File tmp = new File("tmp");
		if (tmp.exists())
			FileUtils.deleteDirectory(tmp);
		FileUtils.forceMkdir(servicePath);
		FileUtils.forceMkdir(libPath);
		FileUtils.forceMkdir(tmp);
	}

	/**
	 * deploy a new service into ganjex service directory
	 *
	 * @param path path of the source code of the service
	 * @param name service name
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void deployService(String path, String name) throws IOException, InterruptedException {
		File tmpSrc = new File("tmp/" + name + "/src");
		File tmpOut = new File("tmp/" + name + "/out");
		FileUtils.deleteDirectory(tmpOut);
		FileUtils.deleteDirectory(tmpSrc);
		FileUtils.deleteDirectory(new File("tmp/" + name));
		copyAndCompile("service", path, tmpSrc, tmpOut);
		commandRun("jar -cf " + name + ".jar -C out/ .", tmpOut.getParentFile());
		FileUtils.copyFileToDirectory(new File(tmpOut.getParent(), name + ".jar"),
						new File(config.getServicePath()), false);
		boolean modified = new File(config.getServicePath(), name + ".jar")
						.setLastModified(System.currentTimeMillis() + 10);
		if (!modified)
			log.error("could not set the lastModified date of the newly created service");
	}

	/**
	 * remove a service from the service location of the ganjex
	 *
	 * @param name service name
	 * @throws IOException
	 */
	public static void unDeployService(String name) throws IOException {
		String path = config.getServicePath() + "/" + name + ".jar";
		File serviceJar = new File(path);
		if (!serviceJar.delete())
			throw new IOException("could not delete " + path);
	}

	/**
	 * deploy a new library into ganjex library location
	 *
	 * @param path source code path of the library
	 * @param name library name
	 * @throws IOException          if it cannot create or modified files under directories related
	 * @throws InterruptedException if it cannot create a jar file of the compiled library
	 */
	public static void deployLib(String path, String name) throws IOException, InterruptedException {
		File tmpSrc = new File("tmp/" + name + "/src");
		File tmpOut = new File("tmp/" + name + "/out");
		copyAndCompile("lib", path, tmpSrc, tmpOut);
		commandRun("jar -cf " + name + ".jar -C out/ .", tmpOut.getParentFile());
		FileUtils.copyFileToDirectory(new File(tmpOut.getParentFile(), name + ".jar"),
						new File(config.getLibPath()), false);
		boolean modified = new File(config.getLibPath(), name + ".jar")
						.setLastModified(System.currentTimeMillis() + 10);
		if (!modified)
			log.error("could not set the lastModified date of the newly created library");
	}

	/**
	 * wait until {@link Ganjex} indicate the ganjex bootstrap process completed
	 *
	 * @throws InterruptedException if the current thread interrupted while sleeping
	 */
	public static void waitToBootstrap() throws InterruptedException {
		while (!Ganjex.bootstrapped())
			Thread.sleep(500);
	}

	private static void copyAndCompile(String extType, String srcPath, File destFile, File outFile)
					throws IOException {
		if (outFile.exists())
			FileUtils.deleteDirectory(outFile);
		if (!outFile.mkdirs())
			log.error("could not make directory {}", outFile);
		copyAllContent(new File(srcPath), destFile);
		deleteAllFilesInDirectory(destFile, new String[]{"java", "class", "jar"});
		Collection<File> libFiles = FileUtils.listFiles(destFile, new String[]{extType}, true);
		libFiles.forEach(file -> {
			if (!file.renameTo(new File(file.getAbsolutePath()
							.substring(0, file.getAbsolutePath().length() - (extType.length() + 1)) + ".java")))
				log.error("can not rename file: {}", file.getAbsolutePath());
		});
		Collection<File> sourceFiles = FileUtils.listFiles(destFile, new String[]{"java"}, true);
		List<String> args = new ArrayList<>();
		String cp = new File(".").getCanonicalPath() + File.separator + config.getLibPath();
		File libFolder = new File(cp);
		File[] jars = libFolder.listFiles(new JarFilter());
		if (jars != null && jars.length > 0) {
			args.add("-cp");
			args.add(Arrays.stream(jars).map(file -> {
				try {
					return file.getCanonicalPath();
				} catch (IOException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.joining(";")));
		}
		args.add("-d");
		args.add(outFile.getPath());
		args.addAll(sourceFiles.stream().map(File::getPath).collect(Collectors.toList()));
		System.out.println(args);
		ToolProvider.getSystemJavaCompiler().run(null, null, null, args.toArray(new String[0]));
		copyAllContent(destFile, outFile);
		deleteAllFilesInDirectory(destFile, new String[]{"java"});
	}

	private static void deleteAllFilesInDirectory(File dir, String[] extentions) {
		Collection<File> filesToDelete =
						FileUtils.listFiles(dir, extentions, true);
		filesToDelete.forEach(f -> {
			if (!f.delete())
				log.error("could not delete file {}", f.getAbsolutePath());
		});
	}

	private static void copyAllContent(File src, File dest) throws IOException {
		File[] allSourceFiles = src.listFiles();
		if (!src.exists())
			if (!src.mkdir())
				throw new IOException("can not create directory " + src.getPath());
		if (Objects.isNull(allSourceFiles))
			throw new IllegalArgumentException("srcPath is not correct: " + src.getPath());
		for (File f : allSourceFiles) {
			if (f.isDirectory())
				FileUtils.copyDirectoryToDirectory(f, dest);
			else
				FileUtils.copyFileToDirectory(f, dest, false);
		}
	}

	private static void commandRun(String command, File workDir)
					throws IOException, InterruptedException {
		CommandLine cm = CommandLine.parse(command);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setWorkingDirectory(workDir);
		DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
		executor.execute(cm, handler);
		handler.waitFor();
	}

}
