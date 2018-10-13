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

![alt text](binder.jpg)
