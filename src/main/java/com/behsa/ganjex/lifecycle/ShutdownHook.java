package com.behsa.ganjex.lifecycle;

import com.behsa.ganjex.api.ServiceContext;

import java.util.function.Consumer;

/**
 * inner representation of the methods in the libraries which annotated with the
 * {@link ShutdownHook} annotation
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
public class ShutdownHook implements Comparable<ShutdownHook> {
	private final Consumer<ServiceContext> hook;
	private Integer priority = 100;

	public ShutdownHook(Consumer<ServiceContext> hook) {
		this.hook = hook;
	}

	Consumer<ServiceContext> hook() {
		return hook;
	}

	@Override
	public int compareTo(ShutdownHook o) {
		return priority.compareTo(o.priority);
	}

}
