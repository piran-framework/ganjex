package com.behsa.ganjex.api;

import java.lang.annotation.*;

/**
 * This is the startup hook annotation, it used by the ganjex library to register their hook
 * which should be executed right after a new service loaded
 * <p>
 * every method annotated with {@link StartupHook} should surrounded with a class which contain a
 * default constructor and accept just one parameter of type {@link ServiceContext}, this
 * parameter indicate some information about the service, also containing service class loader
 * which hook can use to find element in the service
 *
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StartupHook {
	/**
	 * the priority of this hook, low number executed with high priority, it's better to not use 0
	 * and any other low number if it's not very necessary
	 *
	 * @return the priority of this hook
	 */
	int priority() default 0;
}
