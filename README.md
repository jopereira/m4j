# Java support for Maelstrom

Support library for developing [Maelstrom](https://github.com/jepsen-io/maelstrom) nodes with Java. This include three main things:

* a `Network` class that allows reading and writing properly formatted messages from stdin and stdout (using Jackson for JSON I/O);
* a optional `Node` class that handles "init" messages and supports concurrent execution of handlers, timeouts, and other asynchronous evens with an Executor;
* `record` definitions for basic and workload message formats used by Maelstrom. 

See https://jitpack.io/#jopereira/m4j for Maven/Gradle/... dependency configuration information.

The library uses [slf4j](https://www.slf4j.org/) for logging and expects an implementation and configuration.

See source code for API documentation.
