package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;

import java.util.function.Consumer;

/**
 * inner representation of the methods in the libraries which annotated with the
 * {@link StartupHook} annotation
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class StartupHook implements Comparable<StartupHook> {
	private final Consumer<ServiceContext> hook;
	private Integer priority = 100;

	public StartupHook(Consumer<ServiceContext> hook) {
		this.hook = hook;
	}

	Consumer<ServiceContext> hook() {
		return hook;
	}

	@Override
	public int compareTo(StartupHook o) {
		return priority.compareTo(o.priority);
	}
}
