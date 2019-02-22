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

package com.piranframework.ganjex.container;

import com.piranframework.ganjex.api.ServiceContext;
import com.piranframework.ganjex.api.ShutdownHook;
import com.piranframework.ganjex.api.StartupHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * {@link HookLoader} is a class responsible for loading all hooks in the hook objects provided in
 * the {@link com.piranframework.ganjex.api.GanjexConfiguration} and registering all of them into
 * {@link com.piranframework.ganjex.lifecycle.LifecycleManagement} instance.
 *
 * @author hekmatof
 * @since 1.0
 */
public final class HookLoader {
    private final Logger log = LoggerFactory.getLogger(HookLoader.class);
    /**
     * Container configuration and state object also useful to get <code>lifecycleManagement</code>
     * instance.
     */
    private final GanjexApplication app;

    /**
     * Constructs a new <code>HookLoader</code> with the ganjex container instance passed to it.
     *
     * @param app ganjex container state object.
     */
    public HookLoader(GanjexApplication app) {
        this.app = app;
    }

    /**
     * Loads all the methods in the hook objects and register them in the
     * {@link com.piranframework.ganjex.lifecycle.LifecycleManagement} instance of the container.
     */
    public void loadHooks() {
        Stream.of(app.config().getHooks()).forEach(this::registerHooksOfObject);
    }

    private void registerHooksOfObject(Object object) {
        Stream.of(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(StartupHook.class))
                .forEach(method -> app.lifecycleManagement().registerStartupHook(
                        new com.piranframework.ganjex.lifecycle.StartupHook(createHook(method, object),
                                method.getAnnotation(StartupHook.class).priority())));
        Stream.of(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(ShutdownHook.class))
                .forEach(method -> app.lifecycleManagement().registerShutdownHook(
                        new com.piranframework.ganjex.lifecycle.ShutdownHook(createHook(method, object),
                                method.getAnnotation(ShutdownHook.class).priority())));
    }

    private Consumer<ServiceContext> createHook(Method m, Object o) {
        return (ServiceContext s) -> {
            try {
                m.invoke(o, s);
            } catch (IllegalAccessException e) {
                log.error("could not execute hook {} of class {} for service {} version{} because the " +
                                "hook method is not accessible", m.getName(),
                        m.getDeclaringClass().getName(), s.getName(), s.getVersion());
            } catch (InvocationTargetException e) {
                log.error("could not execute hook {} of class {} for service {} version {}, cause:",
                        m.getName(), m.getDeclaringClass().getName(), s.getName(), s.getVersion(),
                        e.getTargetException());
            }
        };
    }
}
