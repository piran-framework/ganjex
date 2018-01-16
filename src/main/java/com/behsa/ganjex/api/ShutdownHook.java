package com.behsa.ganjex.api;

import java.lang.annotation.*;

/**
 * @author Esa Hekmatizadeh
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ShutdownHook {
	/**
	 * the priority of this hook, low number executed with high priority, it's better to not use 0
	 * and any other low number if it's not very necessary
	 *
	 * @return the priority of this hook
	 */
	int priority() default 100;
}
