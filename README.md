# here be dragons

**summer-flip-flop** is my scratch project for experimenting with various libraries, architectures, or language features

### gutes Delirium architektur

may or may not include the following:

- hexagonal architecture
- asynchronous / reactive
- actor model pattern
- uses vertx event bus function adapters. binder is an utility class that:
  - produces an adapter function that can be injected (question / event bus sender)
  - produces a verticle that requires a concrete function (answerer / event bus consumer)

#### module structure

pretty much every module depends on the core module, and dependency inversion is achieved both at the implementation level as well as the module level.

i.e. the api does not depend on the system, only the implementation of the api does, and this is reflected in the module dependencies.

![modules diagram](modules.jpg)

#### connector-vertx-bus and connector-vertx-common

they allow replacing the traditional interactions between the layers with a question-answer event bus

given a concrete function of shape `(Q) -> A` and an address, we define a binder that will produce:
  - another function of shape `(Q) -> A` that, when invoked, will send `Q` through the event bus at this address
  - a consumer that listens on the event bus at this address and invoke the concrete function and reply with `A`

so this is just RPC.

![binder diagram](binder.jpg)
