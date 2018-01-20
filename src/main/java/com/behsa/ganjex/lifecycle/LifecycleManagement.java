package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
	private static LifecycleManagement instance;
	private Map<String, ServiceContext> services = new HashMap<>();
	private List<StartupHook> startupHooks = new ArrayList<>();
	private List<ShutdownHook> shutdownHooks = new ArrayList<>();
	private volatile boolean ready = false;

	private LifecycleManagement() {
	}

	/**
	 * LifecycleManagement Class is singleton, this method always return same instance
	 *
	 * @return singleton instance
	 */
	public static LifecycleManagement newInstance() {
		if (Objects.isNull(instance))
			instance = new LifecycleManagement();
		return instance;
	}

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
	 * state that registration is done, all calls of registerShutdownHook and registerStartupHook
	 * after calling this method cause {@link IllegalStateException}
	 *
	 * @throws IllegalStateException if this method ran before
	 */
	public void doneRegistering() {
		if (ready)
			throw new IllegalStateException("call doneRegistering method more than once");
		Collections.sort(startupHooks);
		ready = true;
	}

	/**
	 * find a service context by its file name
	 *
	 * @param fileName file name of the service
	 * @return service context if found and null if nothing found
	 */
	public ServiceContext findContext(String fileName) {
		return services.get(fileName);
	}

	/**
	 * register a new service with the context provided and run all the startup hooks against it
	 *
	 * @param context context of the service which want to start
	 */
	public void serviceStarted(ServiceContext context) {
		services.put(context.getFileName(), context);
		startupHooks.forEach(h ->
						h.hook().accept(context));
		log.debug("all startup hooks executed for the service {} version {}", context.getName(),
						context.getVersion());
	}

	/**
	 * run all the shutdown hooks against the service specified by its context, and then remove
	 * this service from the list of services
	 *
	 * @param context context of the service which want to shutdown
	 */
	public void serviceDestroyed(ServiceContext context) {
		shutdownHooks.forEach(h -> h.hook().accept(context));
		log.debug("all shutdown hooks executed for the service {} version {}", context.getName(),
						context.getVersion());
		services.remove(context.getFileName());
	}

	/**
	 * useful for testing, destroy lifecycleManagement instance and clean all the registered hooks
	 */
	public void destroy() {
		services = new HashMap<>();
		startupHooks = new ArrayList<>();
		shutdownHooks = new ArrayList<>();
		instance = null;
		ready = false;
	}
}
