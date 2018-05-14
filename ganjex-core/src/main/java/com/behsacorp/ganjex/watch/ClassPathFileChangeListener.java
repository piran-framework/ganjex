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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.behsacorp.ganjex.container.GanjexApplication;
import com.behsacorp.ganjex.lifecycle.ServiceDestroyer;
import com.behsacorp.ganjex.lifecycle.ServiceStarter;

/**
 * The <b>ClassPathFileChangeListener</b> class by implementing
 * {@link FileChangeListener} is a listener of changes in the services
 * directory. This listener create {@link ServiceStarter} and
 * {@link ServiceDestroyer} instance for each service added(or modified) or
 * removed from the directory and call the deploy or destroy method of that
 * objects
 * <p>
 * An instance of this class should be assign to {@link JarWatcher} constructor
 * as a listener.
 *
 * @author omidp
 * @see ServiceStarter
 * @see ServiceDestroyer
 * @see FileChangeListener
 * @since 1.0
 */
public class ClassPathFileChangeListener implements FileChangeListener {
	private static final Logger log = LoggerFactory.getLogger(ClassPathFileChangeListener.class);
	private final GanjexApplication app;

	public ClassPathFileChangeListener(GanjexApplication app) {
		this.app = app;		
	}

	private void createArchive() {
		int cnt = 0;
		for (String cp : app.config().getClassPaths()) {
			log.info("creating service jar file for path {}", cp);
			String name = cnt + ".jar";
			JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
			JavaArchive explodedArchive = ShrinkWrap.create(ExplodedImporter.class, name).importDirectory(new File(cp))
					.as(JavaArchive.class);
			archive.merge(explodedArchive);
			archive.as(ZipExporter.class)
					.exportTo(new File(app.config().getServicePath() + File.separator + name), true);
			cnt++;
		}

	}

	@Override
	public void fileAdd(File file) {
		log.info("new file added to jar {}", file.getName());
		createArchive();
	}

	@Override
	public void fileRemoved(File file) {
		log.info("service {} is removed", file.getName());
		createArchive();
	}

}
