# What is InsDog (a.k.a Inspektor Dog) ?

**InsDog** is an action-based test runner extension for JUnit written in **Java**.
It is intended to deliver seamless experiences to product test developers to write tests in production and higher-level integration tests.

Perhaps, you have already seen several unfamiliar keywords and have questions such as "action-based", "higher-level integration tests", why Java, as such.
Let's walk them through one-by-one.

## Developer Friendly and Why Java?

The main goal of **InsDog** is "Developer Friendly" testing framework.

We don't use an independent "DSL" for writing tests.
Instead, we just employ a full-fledged programming language to write tests, yet providing "framework" so that programmers can naturally learn how to write good tests with minimum cost.

Specifications change over time and test suites grow.
Without conducting refactoring over test suites, they will become unmanageable inevitably.

Test writers are able to refactor them using their familiar IDE and its out-of-box functionalities to refactor the source code.

Not only that, we don't want to impose developers to learn extra programming language.
Because except for limited number of tech giants, corporates in the industry simply cannot afford it.

Here is why the answer for "Why Java?".

Because it is one of the programming Linguae Francae.
Because JVM runs many languages such as Python, Ruby, Scala, Kotlin, etc.
Many languages can be compiled into JVM bytecodes.

Developers can write tests in the same programming language as the one they write their production code.

## Action-based?

Another main characteristics of **InsDog** is being "action-based".

When you automate tests, you realize that you want to make the same code serve for different purposes.
For setting up test target applications, or for executing the test itself.
Once you write a test, it will be a precondition of another tests.
Sometimes one component requires other components to be installed with proper data sets.
But obviously this will be always very expensive for significantly complex systems.

If we want to automate it the steps reusing already written codes, it means the order, where such actions will be performed, needs to be determined at runtime. 
Not compile time.

This is why we need to express what we are performing against the SUT and the execution environment as "actions", objects which can be performed in a programmatic way.

This delivers other benefits.
Reporting and execution flexibility.
You will be looking into how the mechanism contributes to these characteristics.
For now, we just state that it allows us to implement fancy features such screen-shooting before and after a test method, displaying an action tree, which indicates where the failure is at during a test execution at a glance.

## "Tests in Higher-Integration Level" and Why It Matters?

Tests in "Higher-Integration Level", or Higher-Integration tests are terms we coined.
They refer to tests executed inside a component's repository using `failsafe` plugin.
When we mention "Higher-Integration Tests", it means tests across multiple components and/or multiple developers.
So called "end-to-end" tests are one form of it.

Refactoring inside one repository can be verified by unit tests or integration tests (in **Maven**'s sense).

In reality, architecture level decisions need to be modified over time.
For instance, you may want to replace a mechanism to poll another system/component with a more modern mechanism, where **Kafka** is employed.

In this situation, multiple home-grown components interact, and they need to be set up beforehand.
In the context of automated testing and continuous integration/continuous delivery, these need to happen fully automatically.


**InsDog** is a powerful tool to arrange and perform tests in such a situation because of its protocol neutral feature at the framework level.

## Web UI Support

Although **InsDog** is designed language and protocol neutral, it has an out-of-box support for Web UI leveraged by [Playwright for Java](https://playwright.dev/java/docs/intro).
With that, you can start writing your own end-to-end tests quickly.

# Let's Dive!

Still it's not clear for you?
To help you understand, we wrote this "Lessons".
In each lesson, we try to provide working examples and their design background so that you can understand the concepts.
Note that code examples and output examples are as of the time, where this tutorial session is being written.
So, sometime, minor details can be different from the most recent specification and design.
Also, we skipped methods/variables not related to essential discussions in a lesson for the simplicity's sake.
Refer to the package `jp.co.moneyforward.autotest.lessons` under `src/test/java`.
It contains a set of example classes, which actually implement examples used in this session.

Or if your team already uses **InsDog** and you just want to add/improve tests? 
That's great!
You can start working on by mimicking/modifying existing ones.
When you come to want to understand **InsDog**'s design and do better jobs.
Come back here and start reading, anytime.

Anyways, let's dive in!
