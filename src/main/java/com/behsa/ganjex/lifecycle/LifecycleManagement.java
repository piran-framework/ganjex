package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class LifecycleManagement {

	private static final Logger log = LoggerFactory.getLogger(LifecycleManagement.class);
	private List<StartupHook> startupHooks = new ArrayList<>();
	private List<ShutdownHook> shutdownHooks = new ArrayList<>();
	private volatile boolean ready = false;

	public void registerStartupHook(StartupHook startupHook) {
		if (ready)
			throw new IllegalStateException("registering hook after startup is not supported");
		startupHooks.add(startupHook);
	}

	public void registerShutdownHook(ShutdownHook shutdownHook) {
		if (ready)
			throw new IllegalStateException("registering hook after startup is not supported");
		shutdownHooks.add(shutdownHook);
	}

	public void doneRegistering() {
		if (ready)
			throw new IllegalStateException("call doneRegistering method more than once");
		Collections.sort(startupHooks);
	}

	public void serviceStarted(ServiceContext context) {
		startupHooks.forEach(h ->
						h.hook().accept(context));
		log.debug("all startup hooks executed for the service {} version {}", context.getName(),
						context.getVersion());
	}

	public void serviceDestroyed(ServiceContext context) {
		shutdownHooks.forEach(h -> h.hook().accept(context));
		log.debug("all shutdown hooks executed for the service {} version {}", context.getName(),
						context.getVersion());
	}
}
