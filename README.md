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


#### what is this diagram

given a concrete function of shape `(Q) -> A` and an address, we define a binder that will produce:
  - another function of shape `(Q) -> A` that will send `Q` through the event bus at this address
  - a consumer that listens on the event bus at this address and invoke the concrete function

so this is just RPC.

![alt text](binder.jpg)
