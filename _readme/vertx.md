#### connector-vertx-bus and connector-vertx-common

uses vertx event bus function adapters. binder is an utility class that:
- produces an adapter function that can be injected (question / event bus sender)
- produces a verticle that requires a concrete function (answerer / event bus consumer)

they allow replacing the traditional interactions between the layers with a question-answer event bus

given a concrete function of shape `(Q) -> A` and an address, we define a binder that will produce:
  - another function of shape `(Q) -> A` that, when invoked, will send `Q` through the event bus at this address
  - a consumer that listens on the event bus at this address and invoke the concrete function and reply with `A`

so this is just RPC.

![binder diagram](assets/binder.jpg)
