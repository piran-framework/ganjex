package com.behsa.ganjex.e2e;

import com.behsa.ganjex.bootstrap.Bootstrap;
import com.behsa.ganjex.config.Config;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Test utilities for ganjex
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class TestUtil {
	/**
	 * directory where all the e2e test placed relative to the root of project
	 */
	public static final String TEST_PATH = "src/test/java/com/behsa/ganjex/e2e/";
	/**
	 * test config file location relative to the root of project
	 */
	public static final String TEST_CONFIG_PATH = "src/test/resources/config-test.properties";
	public static final long TIMEOUT = 3000L;
	private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

	/**
	 * prepare config with {@link TestConfiguration}, delete the tmp location and delete service
	 * path and library path
	 *
	 * @throws IOException
	 */
	public static void clean() throws IOException, InterruptedException {
		Config.setConfig(new TestConfiguration());
		File servicePath = new File(Config.config().get("service.path"));
		File libPath = new File(Config.config().get("lib.path"));
		deleteAllFilesInDirectory(servicePath,new String[]{"jar"});
		deleteAllFilesInDirectory(libPath,new String[]{"jar"});
//		FileUtils.deleteDirectory(servicePath);
//		FileUtils.deleteDirectory(libPath);
		File tmp = new File("tmp");
		FileUtils.deleteDirectory(tmp);
		Bootstrap.destroy();
	}

	/**
	 * deploy a new service into ganjex service location
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
						new File(Config.config().get("service.path")));
	}

	/**
	 * remove a service from the service location of the ganjex
	 *
	 * @param name service name
	 * @throws IOException
	 */
	public static void unDeployService(String name) throws IOException {
		String path = Config.config().get("service.path") + "/" + name + ".jar";
		File serviceJar = new File(path);
		if (!serviceJar.delete())
			throw new IOException("could not delete " + path);
	}

	/**
	 * deploy a new library into ganjex library location
	 *
	 * @param path source code path of the library
	 * @param name library name
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void deployLib(String path, String name) throws IOException, InterruptedException {
		File tmpSrc = new File("tmp/" + name + "/src");
		File tmpOut = new File("tmp/" + name + "/out");
		copyAndCompile("lib", path, tmpSrc, tmpOut);
		commandRun("jar -cf " + name + ".jar -C out/ .", tmpOut.getParentFile());
		FileUtils.copyFileToDirectory(new File(tmpOut.getParentFile(), name + ".jar"),
						new File(Config.config().get("lib.path")));
	}

	/**
	 * wait until {@link Bootstrap} indicate the ganjex bootstrap process completed
	 *
	 * @throws InterruptedException
	 */
	public static void waitToBootstrap() throws InterruptedException {
		while (!Bootstrap.bootstraped())
			Thread.sleep(500);
	}

	/**
	 * invoke a static method of a library
	 *
	 * @param className  full class name which static method belongs to
	 * @param methodName method name
	 * @param returnType return type of the method
	 * @param argType    list of classes indicate the type of the arguments of the method
	 * @param args       method arguments
	 * @param <T>        return type of the method
	 * @return the result of the static method
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static <T> T invokeStaticMethod(String className, String methodName, Class<T> returnType,
																				 Class<?>[] argType, Object... args)
					throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
					IllegalAccessException {
		Class<?> clazz = Bootstrap.libClassLoader().loadClass(className);
		Method method = clazz.getMethod(methodName, argType);
		return returnType.cast(method.invoke(null, args));
	}

	private static void copyAndCompile(String extType, String srcPath, File destFile, File outFile)
					throws IOException {
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
		args.add("-d");
		args.add(outFile.getPath());
		args.addAll(sourceFiles.stream().map(File::getPath).collect(Collectors.toList()));
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
		if (Objects.isNull(allSourceFiles))
			throw new IllegalArgumentException("srcPath is not correct: " + src.getPath());
		for (File f : allSourceFiles) {
			if (f.isDirectory())
				FileUtils.copyDirectoryToDirectory(f, dest);
			else
				FileUtils.copyFileToDirectory(f, dest);
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
