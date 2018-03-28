# jcache
Java Cache implementations

Covered requirements:
 - Thread safe
 - Support for multiple cache strategies
 - Support 2 levels of cache (memory and disk)

Functionalities that need to be implemented:
 - Locking and cleaning of disk stores (locations)
 - Working with multiple cache instances on the same time
 - Expiration for cache items
 - Cache configuration using properties file
 - Investigate keeping generics decoupled from Serializable 
 - Periodically display fill rate (configurable)
 
Covered strategies:
 - LRU
 - LFU
See [this wikipedia article][1] for more cache strategies

[1]: https://en.wikipedia.org/wiki/Cache_replacement_policies
