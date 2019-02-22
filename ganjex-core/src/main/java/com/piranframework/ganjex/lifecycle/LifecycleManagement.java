/*
 * Copyright (c) 2018 Isa Hekmatizadeh.
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

package com.piranframework.ganjex.lifecycle;

import com.piranframework.ganjex.api.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link LifecycleManagement} class is the heart of the services lifecycle management. All of the
 * framework hooks will be registered by the bootstrap process in
 * {@link com.piranframework.ganjex.container.HookLoader} class. After all the registrations are accomplished,
 * the object of this class is ready to handle starting and shutting down the services.
 * <p>
 * This Class Object is responsible for invoking all the startup hooks annotated in each new service found
 * by the ganjex container. If any service is removed, {@link LifecycleManagement} is responsible for invoking
 * shutdown hooks for that service as well.
 * <p>
 * Only one instance of {@link LifecycleManagement} class is enough per each container.
 *
 * @author hekmatof
 * @since 1.0
 */
public final class LifecycleManagement {
    private static final Logger log = LoggerFactory.getLogger(LifecycleManagement.class);
    private final Map<String, ServiceContext> services = new ConcurrentHashMap<>();
    private final List<StartupHook> startupHooks = new ArrayList<>();
    private final List<ShutdownHook> shutdownHooks = new ArrayList<>();
    private volatile boolean ready = false;

    /**
     * Registers a new startup hook.
     *
     * @param startupHook hook representation.
     * @throws IllegalStateException if called after doneRegistering.
     */
    public void registerStartupHook(StartupHook startupHook) {
        if (ready)
            throw new IllegalStateException("registering hook after startup is not supported");
        startupHooks.add(startupHook);
    }

    /**
     * Registers a new shutdown hook.
     *
     * @param shutdownHook hook representation.
     * @throws IllegalStateException if called after doneRegistering.
     */
    public void registerShutdownHook(ShutdownHook shutdownHook) {
        if (ready)
            throw new IllegalStateException("registering hook after startup is not supported");
        shutdownHooks.add(shutdownHook);
    }

    /**
     * States that registration is done. Any calls of <code>registerShutdownHook</code> or
     * <code>registerStartupHook</code> after invoking this method cause {@link IllegalStateException}.
     *
     * @throws IllegalStateException if this method has been already executed.
     */
    public void doneRegistering() {
        if (ready)
            throw new IllegalStateException("doneRegistering method has been called more than once");
        Collections.sort(startupHooks);
        ready = true;
    }

    /**
     * Finds a service context by its file name.
     *
     * @param fileName file name of the service.
     * @return service context if found and null if nothing found.
     */
    ServiceContext findContext(String fileName) {
        return services.get(fileName);
    }

    /**
     * Registers a new service with the context provided and runs all the startup hooks on it.
     *
     * @param context context of the starting service.
     */
    void serviceStarted(ServiceContext context) {
        services.put(context.getFileName(), context);
        startupHooks.forEach(h -> h.hook().accept(context));
        log.debug("all startup hooks executed for the service {} version {}", context.getName(), context.getVersion());
    }

    /**
     * Runs all the shutdown hooks on the service specified by its context, and then removes
     * this service from the list of services.
     *
     * @param context context of the shutting down service.
     */
    void serviceDestroyed(ServiceContext context) {
        shutdownHooks.forEach(h -> h.hook().accept(context));
        log.debug("all shutdown hooks executed for the service {} version {}", context.getName(), context.getVersion());
        try {
            ((URLClassLoader) context.getClassLoader()).close();
        } catch (IOException e) {
            log.error("could not close service {} version {} classloader", context.getName(), context
                    .getVersion());
        }
        services.remove(context.getFileName());
    }

    /**
     * Useful for testing, destroying lifecycleManagement instance and cleaning all the registered hooks.
     */
    public void destroy() {
        services.clear();
        startupHooks.clear();
        shutdownHooks.clear();
        ready = false;
    }

    /**
     * Returns a clone of the list of services.
     *
     * @return all services registered into ganjex container.
     */
    public Collection<ServiceContext> allServices() {
        return new ArrayList<>(services.values());
    }
}
