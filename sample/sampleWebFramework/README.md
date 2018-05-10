# Sample Dynamic Web Framework using Ganjex
In this sample, we show you how to create a dynamic web framework using Spring and Ganjex step by step. We want to develop actual services in different modules, each module may include some action which actually response to requests.
For example HTTP messages to this address:
`http://localhost:8080/helloworld/hello`
processed by the action named `hello` in the module named `helloworld`.
Each module can develop and packaged independently and can be changed anytime without restarting web application. So we 
call this framework _dynamic_

##How Modules look like
Every action in modules should define by an annotation. So we create a separate module named `web-sample-framework-api`.
It does not need any dependency cause it just defined Action annotation:
```
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
	String value();
}
```
value field in Action annotation is action name.

Now we can create or sample module to answer '/helloworld/hello' request. we create a module named 
`web-sample-service-hello`, and add the `web-sample-framework-api` to its dependencies, we use provided scope because we 
provide it in the framework, you will see it later:
```
<dependency>
  <groupId>com.sample</groupId>
  <artifactId>web-sample-framework-api</artifactId>
  <version>1.0-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```

Every module (or Ganjex service) need a manifest.properties file in the classpath, so we create it in resources folder 
with module name and version:
```
name=helloworld
version=1
```

We create actual action in HelloAction class:
```
public class HelloAction {
	@Action("hello")
	public Map<String, Object> hello(Map<String, Object> input) {
		return Collections.singletonMap("result", "Hi " + input.get("name"));
	}
}
```
As you can see here for demonstration propose we use Map<String,Object> to represent the input and output of an action. 
we assume this action contain a parameter "name", concat it with String "Hi" and set it to the output field "result"
It very strait forward.

## Framework, Hard part
we create a module `web-sample-framework` to develop the actual framework. It is a simple spring-boot web application. We 
use Ganjex spring-boot starter to add ganjex, also we need `web-sample-framework-api` and `org.reflections` so we add 
these dependencies:
```
<dependency>
    <groupId>com.behsacorp</groupId>
    <artifactId>ganjex-spring-boot-starter</artifactId>
    <version>0.3-RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.sample</groupId>
    <artifactId>web-sample-framework-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.9.10</version>
</dependency>
```
As a regular spring-boot application we need a Main Class, we add @EnableGanjexContainer to this class to enable ganjex:
```
@SpringBootApplication
@EnableGanjexContainer
public class SampleFramework {
	public static void main(String[] args) {
		SpringApplication.run(SampleFramework.class, args);
	}
}
```
We add three properties in application.properties:
```
ganjex.lib-path=../../../dist/libs #it's relative path for libraries and maybe you want to change it
ganjex.service-path=../../../dist/services #it's relative path for services and maybe you want to change it
ganjex.watch-delay=4
```
We need a GanjexHook class to handle adding and removing modules. Here is its source code
```
@GanjexHook
public class ServiceContainer {
	private static final Logger log = LoggerFactory.getLogger(ServiceContainer.class);
	private final Map<String, Map<String, Function<Map<String, Object>, Map<String, Object>>>>
					actions = new HashMap<>();
	private final Map<Class<?>, Object> instances = new HashMap<>();

	@StartupHook
	public void add(ServiceContext context) {
		Reflections.log = LoggerFactory.getLogger(Reflections.class);
		Reflections reflections = new Reflections(new MethodAnnotationsScanner(),
						context.getClassLoader());
		Set<Method> actionMethods = reflections.getMethodsAnnotatedWith(Action.class);
		Map<String, Function<Map<String, Object>, Map<String, Object>>> actionsOfTheModule = new
						HashMap<>();
		actionMethods.forEach((Method m) -> {
			if (!instances.containsKey(m.getDeclaringClass())) {
				try {
					instances.put(m.getDeclaringClass(), m.getDeclaringClass().getConstructor().newInstance());
				} catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
								InvocationTargetException e) {
					log.error("error in initializing class {}", m.getDeclaringClass(), e);
				}
			}
			Action action = m.getAnnotation(Action.class);
			actionsOfTheModule.put(action.value(), input -> {
				try {
					return (Map<String, Object>) m.invoke(instances.get(m.getDeclaringClass()), input);
				} catch (IllegalAccessException | InvocationTargetException e) {
					log.error("error invoking method {} of class {}", m, m.getDeclaringClass(), e);
					return null;
				}
			});
			log.info("action {} of module {} added", action.value(), context.getName());
		});
		actions.put(context.getName(), actionsOfTheModule);
	}

  @ShutdownHook
	public void remove(ServiceContext context) {
		actions.remove(context.getName());
	}

	public Function<Map<String, Object>, Map<String, Object>> get(String moduleName,
																																String actionName) {
		Map<String, Function<Map<String, Object>, Map<String, Object>>> moduleMap =
						actions.get(moduleName);
		if (Objects.isNull(moduleMap))
			throw new IllegalStateException();
		Function<Map<String, Object>, Map<String, Object>> function = moduleMap.get(actionName);
		if (Objects.isNull(function))
			throw new IllegalStateException();
		return function;
	}
}
```
Don't afraid, the heart of this class is this data structure:
```
Map<String, Map<String, Function<Map<String, Object>, Map<String, Object>>>> actions = new HashMap<>();
```
This Map store all the information we need about modules and their action, I know it's not pretty and efficient but
remember it's just demonstration. The first outer map key is module name and its value is another map representing the module 
actions, the key of this second map is action names and their value is a Function, representing actual action method with 
its input and output (remember from HelloAction class, input and output of the action is Map<String,Object>)
 
All We need to do here is to manipulate this map and query it when needed. This map should be manipulated when a 
ganjex service added or removed, because of that we annotate this class as a @GanjexHook and annotate two methods with 
@StarupHook and @ShutdownHook. when a ganjex service jar file located in the service directory 
(we define it in application.properties) ganjex invoke methods annotated with @StartupHook and when a service jar file 
removed from services directory it invokes @ShutdownHook methods.
 
Here is the ServiceContainer class when a service added we get service classloader from service context and by using org.reflection we search 
for methods annotated with @Action we defined before and fill the actions map. and remove actions map entity when a 
service removed.

## Dispatcher, Final part 
The system needs a way to get input, yes we need a controller to get any HTTP request and invoke its related action 
and route back the response. We use `get` method of the ServiceContainer class to find related actions.
```
@RestController
public class Dispatcher {
	private final ServiceContainer container;

	@Autowired
	public Dispatcher(ServiceContainer container) {
		this.container = container;
	}

	@RequestMapping(value = "{moduleName}/{action}", method = {RequestMethod.GET, RequestMethod.POST,
					RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<?> dispatch(@PathVariable String moduleName,
																		@PathVariable String action,
																		@RequestBody Map<String, Object> object) {
		try {
			return ResponseEntity.ok(container.get(moduleName, action).apply(object));
		} catch (IllegalStateException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
```
##Build and run it
Use maven to package `web-sample-framework` by the command:
```
mvn clean package
```
now you can run the created jar file.
```
java -jar target/*.jar
```
After running our application we can build `web-sample-service-hello` by maven and copy the created jar file into 
service directory. application detect it, now you can send http request and see the response. you can also change hello 
service and redeploy it and retry, you see the changes detected dynamically and application behavior changed dynamically 
without any restarting.

##Conclusion
Was it hard? maybe, but was it worth? It depends on your requirements. remember you have created a dynamic framework, 
of course creating a framework is hard but has its own benefits, you can create frameworks which instead of HTTP 
request, pull request from a queue(Kafka or rabbitMQ or whatever else) and process it by dynamic actions you can develop 
and manage independently, it sounds great, isn't it?