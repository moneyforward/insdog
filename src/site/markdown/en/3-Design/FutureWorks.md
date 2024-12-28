# Future Works

In this section, we discuss potential development items for the future.
Any of them is commited to be developed as of now until a ticket for it is filed in the community.

## Splitting Modules

Right now, **InsDog** contains only one module: `insdog-all`, which depends on **Playwright for Java**.
However, this is not necessarily always required.
Only when you are testing web application UIs, it will be needed.

In the future, we will have the following submodules on top of `insdog-all`, which always contains all of them.

* `framework`
* `webui`

Maybe we have more modules such as `grpc`, `rest`, `python`, etc.

## Multi-Language Support

JVM (**Java Virtual Machine**) can run programs written in any bytecode compilable languages.
So, without modifying any single line of codes in the framework, you can run tests written in **Kotlin**, for instance.
But it will require a bit tedious job if we try other languages such as **Python**  to abstract its language characteristics.
Rather than having our users write adapters, it may be a good idea to bundle a helper class in **InsDog** itself.

## API test support

Similar to multi-language support, users of **InsDog** can develop their own base classes and utilities for API testing for protocols such as `gRPS`, `REST`, etc., by themselves.
It would be repetitive and tedious job.
It will be a good idea to integrate basic supports for API tests of popular protocols is a good idea.

## Reverse Execution Mode

This is the third execution mode, where actions specified by `@AutotestExecution#defaultExecution(): @Spec#value` is executed but in the reversed order unless it breaks the constraint defined by `@DependsOn` and `@When`.

This will be useful to find bugs in the product that can be revealed only by the order of executions.
If you are interested in the basis (theory) of this idea, check Chapter 10 "Sequence-Covering Arrays" of "Introduction to Combinatorial Testing" by D. Richard Kuhn, Raghu N. Kacker, and Yu Lei (CRC Press).