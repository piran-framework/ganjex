# Ganjex
Ganjex is a passive container which know nothing about the services it contains. Ganjex
facilitate runtime changing and lifecycle management of services. Ganjex is a platform layer 
container, users should define their framework to use it properly. There is two 
type of element which ganjex contain: 1.Library and 2.Service.

## Framework
Client should define how it wants to treat services, we call client application framework. 
Framework start the ganjex container with the code below:
 ```
 Ganjex.run(ganjexConfiguration);
 ``` 
 In the above code `ganjexConfiguration` is a configuration instance of `GanjexConfiguration` 
 class which should created by the mean of `GanjexConfiguration.Builder`. for example:
 ```
 	Ganjex ganjex = //you can save an instance of the ganjex container in the ganjex object
 	    Ganjex.run(new GanjexConfiguration.Builder()
 	        .libPath("/opt/ganjex/lib")         //location where libraries should be added to
 	        .servicePath("/opt/ganjex/service)  // location where services should be added to
 	        .watcherDelay(4)                    //how many second watchers should wait to retry
 	        .hooks(new SomeHookContainer())     //list of all object containing hooks
 	        .build());
 ``` 
note that `SomeHookContainer` class in the above example should have some method annotated by 
`@StartupHook` or `@ShutdownHook`. This is an example of this class:
```
public class SomeHookContainer {
    @StartupHook
    public void start(ServiceContext context){
        //change some behavior with newly added service
    }
    
    @ShutdownHook
    public void destroy(ServiceContext context){
        //fallback changes made when this service added
    }
} 
```
Usually you need the service classLoader to surf the service code and find things you know so we 
provide service classLoader by the ServiceContext object.

Remember frameworks does not change a lot.

## Service     
Services are changeable units, which should do a specific job, typically implementing a use case.       
As in business, use cases changes a lot, in ganjex services usually changes a lot too. When a 
requirement changes, the implementation of that requirement should be changed and should be 
deployed as soon as possible, so services in ganjex can be deployed or removed on runtime (on the
fly). When a service added to the ganjex container all of the `@StartupHook`s  notified with 
the `ServiceContext` of the newly added service, And when a service removed from the ganjex 
container all of the `@ShutdownHook`s notified with the `ServiceContext` of that service. It's
up to framework to treat each service and ganjex know nothing about the structure or pattern 
services uses.

### service manifest
Every service should have a file named *manifest.properties* in the root of its classpath. This 
is a manifest clear the service identity, It should contain two keys: *name* and *version*

 
## Library
There are cases when multiple services need a class, or a domain model class should be shared 
between services. As we can have many services, duplicating the shared code in the services is 
not efficient and also hard to maintain. Here Libraries come to rescue, Libraries is a typical 
jar files which can be changed and also it's available to all of the services. Libraries can
change, but remember their changes have its cost, when a library change (modified,added or 
removed) all of the services restart to library change take effect.

Remember when you use a library code in your service, mark the maven dependency with 
`<scope>provided</scope>` because it provided by the ganjex on runtime.
## Use Spring-Boot and Ganjex together
There is a spring-boot-starter for ganjex. you can add it to your spring-boot application and by 
adding `@EnableGanjexContainer` annotation into your Configuration class, ganjex starts and scan 
all the beans with `@GanjexHook` annotation. Note that when a class marked with `@GanjexHook`, 
that class become a spring component bean, so it's not needed anymore to add @Component or @Service.
### Spring-Boot properties 
Adding ganjex-starter to a spring-boot application add three properties beside spring-boot 
properties:
* ganjex.lib-path
* ganjex.service-path
* ganjex.watch-delay
which is the same as `GanjexConfiguration` fields.

## License
This software is licensed under the Apache License, version 2 ("ALv2"), quoted below.

Copyright 2018 Behsa Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.