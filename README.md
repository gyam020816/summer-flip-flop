# here be dragons

**summer-flip-flop** is my scratch project for experimenting with various libraries, architectures, or language features

may or may not include the following:

- hexagonal architecture
- actor model pattern, more or less
- strict dependency inversion at module level
- asynchronous
  - via reactivex
  - via kotlin coroutines
- liquibase for schema versioning
- database component integration testing using testcontainers
- mockito-kotlin

# quick start

to run the project locally:

- run `./do-linux-local-nonpersistent.sh` on Linux
- run `do-windows-local-nonpersistent.bat` on Windows.

this will expose a PostgreSQL container on port hosts' port 16099.

the service will be running at http://localhost:8080/docs.

### debugging

run the project locally as described in quick start so that the containers will run.

then just start one of the main classes in debug mode with the IDE.

# stuff

#### module structure

pretty much every module depends on the core module that contains common model objects, and dependency inversion is achieved both at the implementation level as well as the module level. the domain api is not part of the core module.

i.e. the api does not depend on the system, only the implementation of the api does, and this is reflected in the module dependencies.

![modules diagram](modules.jpg)

#### liquibase

in production, there is a `changelog.xml` that includes other numbered changelog files (i.e. `changelog_001.xml`, `changelog_002.xml`) ...

sometimes we need to add changesets with impact on the existing data such as non-nullable columns

in order to test these changelog files, there are two tests:

- one which executes the production `changelog.xml` with no initial data
- one which executes a test file `changelog_migration_test.xml` which includes the aforementionned production numbered changelog files, with with some data insertion the changesets i.e.  (i.e. `changelog_001.xml`, `initial_data_after_1.sql`, `changelog_002.xml`)

the test for the data access object has a reference to the production code responsible for upgrading the database

![liquibase diagram](liquibaselogs.jpg)

#### reactivex and coroutines

the project started with reactivex, but later on coroutines were added, so now there are coroutines implementations

sadly there is an asynchronous footprint in the function signatures and interfaces i.e. `RxDocStorage` and `SDocStorage` are two interfaces that expose the same API

what to do if a coroutine-based class needs to call a reactive-based adapter?

well, what's worse than bridging the two worlds with adapters?

- `ReactiveToSuspendedDocStorage` exposes a Reactive `RxDocStorage` and consumes a coroutine-based `SDocStorage`
- `SuspendedToRxDocSystem` exposes a coroutine-based `SuspendedDocSystem` and consumes a coroutine-based `RxDocSystem`

nothing wrong here

![coreactinex diagram](coreactinex.jpg)

#### connector-vertx-bus and connector-vertx-common

uses vertx event bus function adapters. binder is an utility class that:
- produces an adapter function that can be injected (question / event bus sender)
- produces a verticle that requires a concrete function (answerer / event bus consumer)

they allow replacing the traditional interactions between the layers with a question-answer event bus

given a concrete function of shape `(Q) -> A` and an address, we define a binder that will produce:
  - another function of shape `(Q) -> A` that, when invoked, will send `Q` through the event bus at this address
  - a consumer that listens on the event bus at this address and invoke the concrete function and reply with `A`

so this is just RPC.

![binder diagram](binder.jpg)
