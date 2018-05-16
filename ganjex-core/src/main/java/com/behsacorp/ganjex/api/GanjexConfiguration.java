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

package com.behsacorp.ganjex.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This is an immutable class storing the configurations required by Ganjex container. An instance
 * of this class should be passed to <code>{@link Ganjex}.run(GanjexConfiguration) </code> static
 * method or {@link Ganjex} constructor itself. {@link GanjexConfiguration.Builder} class could
 * be used to create an instance of {@link GanjexConfiguration}.
 *
 * @author hekmatof
 * @since 1.0
 */
public final class GanjexConfiguration {
    private final String libPath;
    private final String servicePath;
    private final long watcherDelay;//in second
    private final Object[] hooks;
    private final Set<String> classPaths;

    private GanjexConfiguration(Builder builder) {
        this.libPath = builder.libPath;
        this.servicePath = builder.servicePath;
        this.watcherDelay = builder.watcherDelay;
        this.hooks = builder.hooks;
        this.classPaths = builder.classPaths;
    }


    /**
     * @return class paths where jar files are automatically created.
     */
    public Set<String> getClassPaths() {
        return classPaths;
    }


    /**
     * @return directory path where jar-formatted library files are located.
     */
    public String getLibPath() {
        return libPath;
    }

    /**
     * @return directory path where jar-formatted service files are located.
     */
    public String getServicePath() {
        return servicePath;
    }

    /**
     * @return delay duration of file watcher threads in seconds.
     */
    public long getWatcherDelay() {
        return watcherDelay;
    }

    /**
     * @return list of objects containing {@link StartupHook}s and {@link ShutdownHook}s.
     */
    public Object[] getHooks() {
        return hooks;
    }

    /**
     * Default builder of {@link GanjexConfiguration}
     */
    public static class Builder {
        private String libPath;
        private String servicePath;
        private long watcherDelay;//in second
        private Object[] hooks;
        /**
         * Comma-separated classpath project scans.
         */
        private Set<String> classPaths;

        /**
         * @param libPath directory path where jar-formatted library files are located.
         * @return builder itself.
         */
        public Builder libPath(String libPath) {
            this.libPath = libPath;
            return this;
        }

        /**
         * @param path list of directory paths where the compiled service files are located.
         * @return builder itself
         */
        public Builder classPaths(String[] path) {
            if (classPaths == null)
                classPaths = new HashSet<>();
            if (path != null) {
                classPaths.addAll(Arrays.asList(path));
            }
            return this;
        }

        /**
         * @param servicePath directory path where jar-formatted service files are located.
         * @return builder itself.
         */
        public Builder servicePath(String servicePath) {
            this.servicePath = servicePath;
            return this;
        }

        /**
         * @param watcherDelay delay duration of file watcher threads in second.
         * @return builder itself.
         */
        public Builder watcherDelay(long watcherDelay) {
            this.watcherDelay = watcherDelay;
            return this;
        }

        /**
         * @param hooks list of objects containing {@link StartupHook}s and {@link ShutdownHook}s.
         * @return builder itself.
         */
        public Builder hooks(Object... hooks) {
            this.hooks = hooks;
            return this;
        }

        /**
         * Creates an instance of {@link GanjexConfiguration} and returns it.
         *
         * @return an instance of {@link GanjexConfiguration} created by the builder properties.
         */
        public GanjexConfiguration build() {
            return new GanjexConfiguration(this);
        }

    }
}
