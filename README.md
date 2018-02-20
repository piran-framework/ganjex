## Ganjex Overview
Oblivious to its holding services, Ganjex is a passive container
facilitating the management of the containing elements' alterations and lifecycle at runtime.
Ganjex is a platform layer container, so that user must define their own framework
based on their necessities and preferences properly.   
There are two types of elements Ganjex contains: 1.Library and 2.Service.

### Library
Libraries or frameworks required to be accessible to the services, must be provided to Ganjex before its startup. 
A service needing to do so, should add the library's corresponding Maven dependency with the ```provided``` scope.
In order to manage the lifecycle of libraries' services, Ganjex provides two annotations: 
```@StartupHook``` and ```@ShutdownHook```. 
Libraries are responsible for treating the services, meaning that they scan to find services' requirements and dependencies. 
They also exploit services' class-loaders. This is noteworthy that libraries should handle multi-threading themselves.

### Service
Services could be deployed or removed at runtime. As soon as a service is added to Ganjex container, all 
of the libraries' classes annotated with```@StartupHook``` would be notified with the ```ServiceContext``` of the newly 
added service. Similarly, soon after a service is removed from Ganjex container, all of the libraries' classes annotated 
with ```@ShutdownHook``` would be notified with the ```ServiceContext``` of that service. This is library's responsibility 
to treat each service properly, due to the fact that Ganjex knows nothing of the structure and pattern services utilize.
 

