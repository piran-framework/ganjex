## Ganjex
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.behsacorp/ganjex/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.behsacorp/ganjex)
[![Travis IC](https://travis-ci.org/behsa-oss/ganjex.svg?branch=master)](https://travis-ci.org/behsa-oss/ganjex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5bb12607be964e478f507fd04de0fc21)](https://www.codacy.com/app/esahekmat/ganjex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=behsa-oss/ganjex&amp;utm_campaign=Badge_Grade)
[![Javadocs](http://javadoc.io/badge/com.behsacorp/ganjex.svg)](http://javadoc.io/doc/com.behsacorp/ganjex)

Today In the micro-services world sometimes you need to serve lots of micro-services by just one JVM(runtime), with the help of Ganjex you can define your own microservice framework. When you are developing micro-services, usually every micro service need a dedicated JVM, and any changes in it need restarting JVM. Remember tomcat which contains webapps and you can change webapps by removing or changing WAR files without restarting tomcat itself, but tomcat forces you to define your application in webapp schema, tomcat searches for servlet you defined and route HTTP messages to them. So if you want your micro-services communicating with each other by a queue for example or using gRPC you can't use tomcat simply to serve your micro-services. Also if you want to develop your services in a different manner than webapp and servlet, you can't use tomcat. OSGi is another solution but its too complicated and has a high learning curve. Ganjex like tomcat can contain services, and each service has its own classloader and lifecycle, but unlike tomcat which searches for servlets in its containing webapps, Ganjex is oblivious to its holding services, so we say Ganjex is a passive container just facilitating the management of the containing elements' alterations and lifecycle at runtime. The way this container should behave with its services (remember tomcat which searches for servlet and routes HTTP messages to them) should be defined by the client which uses Ganjex. So we say Ganjex is a platform layer container, which user must define their own framework based on their necessities and preferences properly. There are two types of elements Ganjex contains 1. Library and 2. Service.


## Framework
It is expected that application framework, also called as client, would define how it plans to 
treat containing services. Ganjex container is started by framework with the code below:
 ```
 Ganjex.run(ganjexConfiguration);
 ```
 In the above code `ganjexConfiguration` is a configuration instance of `GanjexConfiguration` 
 class which should be created by `GanjexConfiguration.Builder`. As an illustration:
 ```
 	Ganjex ganjex = //you can save an instance of Ganjex container in Ganjex object
 	    Ganjex.run(new GanjexConfiguration.Builder()
 	        .libPath("/opt/ganjex/lib")         //location where libraries should be added
 	        .servicePath("/opt/ganjex/service)  //location where services should be added
 	        .watcherDelay(4)                    //how many seconds watchers should wait to retry
 	        .hooks(new SomeHookContainer())     //list of all objects containing hooks
 	        .build());
 ``` 
Please note that `SomeHookContainer` class in the above example may have methods annotated with 
`@StartupHook` or `@ShutdownHook` in order to manage the lifecycle of services. This is an 
example of this class:
```
public class SomeHookContainer {
    @StartupHook
    public void start(ServiceContext context){
        //consequent behavior changes mandated by the newly added service
    }
    
    @ShutdownHook
    public void destroy(ServiceContext context){
        //consequent fallback changes mandated by the newly added service
    }
} 
```
The service classLoader is required to surf the service code to manage business necessities 
defined by framework, so the service classLoader would be provided by the ServiceContext object.
In other words, Ganjex would behave in a way that is defined in the framework by the client. 

## Service     
Accomplishing specific jobs, services are interchangeable units typically implement a use case. 
As business use cases are being frequently changed, Ganjex services are supposed to be changed
repetitively as well. As soon as a requirement is changed, the implementation of that requirement must 
be changed and deployed consequently. In Ganjex, also, services could be deployed or removed at runtime 
(on the fly). Soon after a service is added to Ganjex container, all of the `@StartupHook` methods  
would be notified with the `ServiceContext` of the newly added service. Similarly, right after a 
service is removed from Ganjex container, all of the frameworks' methods annotated with 
`@ShutdownHook` would be notified with the `ServiceContext` of that service. 
This is framework's responsibility to treat each service properly, due to the fact that Ganjex 
knows nothing of the structure and pattern services utilize. Remember frameworks are not 
expected to be changed frequently.

### Service manifest
Every service should have a file named *manifest.properties* in the root of its classpath. This 
is a manifest clearing the service identity. This should contain two keys: *name* and *version*

## Library
There are cases when multiple services need a class, or a domain model class should be shared 
between services. As we can have many services, this might bring about having duplicate shared code 
in the services which would be inefficient and difficult to maintain. Here Libraries come to 
rescue, Libraries are typical jar files which can be changed and required to be accessible to the
services. A service needing a library, should 
add the library's corresponding Maven dependency with `<scope>provided</scope>` because it would 
be provided by Ganjex at runtime.

Please note that changing the libraries is costly, meaning that soon after a library is changed 
(modified, added or removed), all of the services would be restarted in order to affect the 
consequent changes.

## Dependency
To use ganjex library in your project add this dependency into your project pom:
```
<dependency>
    <groupId>com.behsacorp</groupId>
    <artifactId>ganjex</artifactId>
    <scope>0.3-RELEASE</scope>
</dependency>
```

## Use Spring-Boot and Ganjex simultaneously
A Spring-boot-starter has been particularly designed for Ganjex which could be mounted on 
Spring-boot applications. By adding `@EnableGanjexContainer` class-level annotation on the 
Configuration class, Ganjex starts and scans all the beans with `@GanjexHook` annotation. Note 
that, if a class is marked with `@GanjexHook`, that class would be qualified to be a Spring 
component bean as well, so there would be no need to add @Component or @Service by doing so.

### Spring-Boot properties 
To add ganjex-starter to a spring-boot application, add three properties besides spring-boot 
properties:
* ganjex.lib-path
* ganjex.service-path
* ganjex.watch-delay
They are the same as `GanjexConfiguration` fields.

### Minimum Requirement
You need at least java 8 to use Ganjex container.

## License
Copyright (c) 2018 Behsa Corporation.

Ganjex is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation, either version 3 of the 
License, or (at your option) any later version.

Ganjex is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU Lesser General 
Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
