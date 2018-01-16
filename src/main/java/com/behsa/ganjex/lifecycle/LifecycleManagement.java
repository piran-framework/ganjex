package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LifecycleManagement class is a heart of the lifecycle management of the services. all of the
 * libraries hook will register by the bootstrap process in
 * {@link com.behsa.ganjex.bootstrap.Bootstrap} class. after all the registration object of this
 * class is ready to handle start and shutdown of the services.
 * <p>
 * every new service found by the
 * ganjex the main instance of this class handle running all the startup hooks for it. and every
 * service removed this class execute all the shutdown hooks for it.
 * <p>
 * just one instance of this class is enough for every container
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class LifecycleManagement {

	private static final Logger log = LoggerFactory.getLogger(LifecycleManagement.class);
	private List<StartupHook> startupHooks = new ArrayList<>();
	private List<ShutdownHook> shutdownHooks = new ArrayList<>();
	private volatile boolean ready = false;

	/**
	 * register a new startup hook
	 *
	 * @param startupHook hook representation
	 * @throws IllegalStateException if called after doneRegistering
	 */
	public void registerStartupHook(StartupHook startupHook) {
		if (ready)
			throw new IllegalStateException("registering hook after startup is not supported");
		startupHooks.add(startupHook);
	}

	/**
	 * register a new shutdown hook
	 *
	 * @param shutdownHook hook representation
	 * @throws IllegalStateException if called after doneRegistering
	 */
	public void registerShutdownHook(ShutdownHook shutdownHook) {
		if (ready)
			throw new IllegalStateException("registering hook after startup is not supported");
		shutdownHooks.add(shutdownHook);
	}

	/**
	 * state that registration is done, all calls od registerShutdownHook and registerStartupHook
	 * after calling this method cause {@link IllegalStateException}
	 *
	 * @throws IllegalStateException if this method ran before
	 */
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
