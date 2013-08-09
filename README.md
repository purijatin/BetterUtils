BetterUtils
===========
A collection of Awesomeness

concurrency.lazyval
===================
In general while using lazy evaluation of variables, we prefer using the Doublic Locking Idiom. In which, the variable is initialized 
in a synchornized block. This can be prone to multiple dead-locks (as found from lazy val evaluation in Scala compiler)

concurrency.lazyval.LazyVal initializes a variable void of synchornization.

Performance Details:
-------------------
Under heavy contention, performs equally to Double Idiom
Under normal contention, it is slightly slower by 5%. 

Please refer wiki for more Information


