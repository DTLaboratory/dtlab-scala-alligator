![Scala CI](https://github.com/SoMind/dtlab-scala-alligator/workflows/Scala%20CI/badge.svg?branch=master)

# Digital Twin Lab - Alligator Version

The DtLab is a distributed programmable actor runtime environment.

  * optimized to enable graphs of digital twins 
  * for IOT and non-IOT applications (neural nets, etc...)
  * programmable via API (cURL)

## Under Construction

## Under Construction

## Under Construction

# Repo Organization and Coding Guidelines

Each repository name has the service name, the computer language implementation, and the release name.  The initial
release names are [animals](https://gist.github.com/navicore/b578e4c6e15d125b1a04ec522e295acf) in alphabetic order (open to better names).  The idea is that a
newer incompatible approach to the service implementation can fork off without the overhead of a git fork.
*See Hickey on semantic versioning "If it is not backward compatible, rename it."*

```
<projectName>-<langName>-<versionName>

eg: dtlab-scala-alligator
```

The build system is (will be) opinionated lint-enabled, `scalafmt` with code stats reports.

API is understood best by reading the tests in `src/scala/test`.

# RUNTIME

  * Akka typed actors
  * Event-sourcing persistence
    * snapshotting meta (prop names) and current state
    * journaling of input - only tuples of (key: Int, value: double), one at a time
  * Akka HTTP REST-like API
  * Kubernetes via CI/CD via Github Actions
  * Monitoring is via Prometheus via `/health` endpoint in each container - same endpoint k8s uses for liveliness/healthcheck probes

# Design

## API

```
TYPE OPERATOR ACTOR LINK
```
*NOTE, only the TYPE API and the ACTOR API will probably be coded in Alligator*

This is the kernel of the DTLab.

It is low-level,internal, and hidden from most applications and users behind higher level APIs (graphql) and tooling.

It does not prepare data from raw telemetry or do any data transformation - telemetry preprocessing is a higher level application.

The concepts are: 

  * Types are collections of named doubles.  
  * Types must be defined before instantiation of instances.
  * The named doubles can be observations or derived (ie: a quality value based on 1 or more current attributes (named doubles) and may need the previous values of those attributes)
  * Derived doubles' processing is defined by the OPERATOR API.
  * DTs are instances of TYPES created with the ACTOR API.
  * Each actor knows its parent and children automatically (via actor runtime / supervision) - there is nothing explicit in the types about parent/child.
  * The LINK API turns the tree of actor instances into a graph.  Once a link is created a linked actor can see the changes to state of the other actor at a specified granularity.  IE: a factory plant actor can see the changes to the state of cars it manufactured at a certain level of granularity, ie: daily. (THIS NEEDS SOME THOUGHT but the changes that are visible via subscription include child actor creation.  Once the aggregate actor (linked actor) is created it will continue to get all the info it needs to watch the evolving system and create new aggregates to watch new sections of the graph.)  
  * ACTOR API is influenced by prototype-based-programming, some new instances of actors will be clones of a prototype actor of some TYPE with OPERATORS.
  * Links are influenced by prototype-based-programming - the runtime will have to be smart enough to resolve some links by peaking at the prototype actor.  The prototype links won't be cloned exactly but used as templates for knowing what the new instance cares about.

### TYPE API

HTTP POST of schemas (ns, type name, and list of attr names whose values are all doubles)
  
  * name is not a path, nothing says where this hangs in the graph - ns is just to manage collisions, global to system
  * all values are doubles (so far)
  * the actor won't know the difference between derived and observed from its type - its state is just list of named doubles

```
ns is location
type is store
attrs are custs_in_store,open_closed

POST /schema/location/store
["customers_in_store", "open_closed"]

```

### OPERATOR API
  
  * Defines how derived values are created.
  * Configuring an actor with a DSL/builtin-func and flag indicating eager or lazy (just-in-time vs always correct) execution - open to expressing in Python, R, etc...
  * Operators will be infinitely powerful in that they can draw on higher level APIs above the kernel APIs - an operator could "search" for example.

  TODO:

  TODO:

### ACTOR API

  * create instances of above types
  * creates are really updates - UPSERTS.
  * creates/updates only init fields if the actor is new - otherwise you must set a field for it to init.
  * creates/updates that have only 1 field will only update that 1 field - supporting telemetry from different sources and late reporters.
  * creates/updates that have only 1 field whose value affects another field via operator triggers recalc of eager operators.
  * query actor state with `/root/<ns.type>/<id>/<ns.type>id/<id>`
  * query actor child name list with `/root/<ns.type>/<id>/<ns.type>id/<id>/children`
  * NOTE, search the actor space is a higher level application and not implemented in the kernel

```
ns is location
type is store
instance is store # 4200
2 custs_in_store open store

POST /actor/location.store/4200
{
  "customers_in_store": 24.0,
  "open_closed": 1.0
}

```

### LINK API

SEE PROTOTYPE BASED PROGRAMMING - links are made to objects, not types

*this is the least worked out but is needed to support a graph and dynamic aggregates and fusion*

TODO:

TODO:


# NOTES

* why not call the ACTOR API the DT API?  will actors always be DTs?  maybe not...
* TYPE might not be the best name for what is really an ordered list of attr names.  Suggestions?
* OPERATOR might not be the best name for what is really event handlers, analogous to RD stored-proc stuff


