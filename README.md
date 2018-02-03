## Ganjex Overview
Ganjex is a passive container which is know nothing about the service it contains. Ganjex
facilitate runtime changing and lifecycle management of services. Ganjex is a platform layer 
container, users should define their framework to use it properly. There is two 
type of element which ganjex contain: 1.Library and 2.Service.

### Library
Libraries or frameworks are the units should be available to the ganjex before it starts, all of 
the libraries are available from the services, if services wants to use libraries they should add
maven dependency to that library with the ```provided``` scope. also there is two hooks 
annotations ganjex provide to the libraries to manage their service lifecycle: 
```@StartupHook``` and ```@ShutdownHook```. Libraries is responsible to treat the services, scan
and find what they want and use them using services classloader. also they should handle 
multithreading themselves.

### Service
services can be deployed or removed on runtime. when a service added to the ganjex container all 
of the ```@StartupHook``` of the libraries notified with the ```ServiceContext``` of the newly 
added service. and when a service removed from the ganjex container all of the 
```@ShutdownHook``` of the libraries notified with the ```ServiceContext``` of that service. it's
 up to library to treat each service and ganjex know nothing about the structure or pattern 
 services uses.
 

