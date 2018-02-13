/**
 * Main module of the ganjex container
 * @author Esa Hekmatizadeh
 * @since 1.0
 */
module ganjex {
	requires java.base;
	requires slf4j.api;
	requires java.compiler;
	requires reflections;
	exports com.behsa.ganjex.api;
}