# Overview

The **InspektorDog** (shortened **InsDog**) is a Test Execution Library, which is:

* **Developer Oriented**
* **A thin and simple library:**
  1. A JUnit Test Runner Extension
  2.  You can use it with arbitrary clients for test development (HttpClient, gRPC, …)
  3.  Design-wise, you can even remove Playwright and replace it with another browser automation solution (although, you don’t want to, perhaps ;) )
* **For Higher Integration Tests**
  1. Inter-component Integration Tests
  2.  End-to-end Tests
* **Action-based**
  1. Execution Flexibility
  2. Rich Reporting
*  and it's **Open Sourced**

1. **Written in Java, not having a separate DSL:**
   It is written in Java in order to allow test-writers to access the most matured knowledge pool among others.
   Also, "correctness" and "defined specifications" are important in verifications and validations, which are what Java is excellent at.
2. **No separate DSL:**
   Introduction of a custom DSL based on external notations, such as CSV, JSON, YAML, Markdown, or whatsoever is what we do not try at this stage.
   We will allow users to program tests only in the host language, **Java**.
   Authors know that it will deliver stunning initial success, and it will decelerate very soon.
   This is caused by various reasons such as lack of language capabilities of the DSL, maintenance cost increased by it from the separation, overuse of syntax-sugars introduced carelessly.
Instead, testers will be allowed to write tests using **Java** programs at this moment.
They can employ all the powerful features the language and its ecosystem have out of the box to make their tests clean and stable, such as inheritance, interface, abstraction, variables, IntelliJ IDEA, vscode, etc. etc. 
3. **Action-based Programming Model:**
Once a test is written, it will be used to prepare a setting for another test.
This is a common situation which authors have experienced.
We can think of a test for "login".
Then, it will be used as a preparation of functions after login, such as search items, print a selected item, follow links in the screen, etc.
Or perhaps, once you tried multiple functions, you may want to exercise them again in the revered order to ensure there is no strange dependency in them.
When a product developer is conducting a refactoring, they only want to run a test step, anything before it are just preparations.
They also want to run a test they focus on currently with minimum dependencies.
In order to make those possible in a programmatic way, test writers are supposed to define what they want to do in a form of an object.
It will be called an action, and in **InsDog**, it will be called `Act`, `Scene`, or `Action`, depending on the context and purposes they fulfil.
4. **Application Neutrality:**
Although it is developed for a certain internal application in **MoneyForward**, it is designed to be able to work for other applications from the day one.
It has a built-in support for Web application's UI testing leveraged by **Playwright**, but it's not limited to the area.
By creating custom scenes and acts, you can apply it to API tests and others, too.
5. **Separation of Tests, Accessing model:**
Similar to the neutrality for applications, it has a concept of **Accessing Model**.
It is a common concern to have a "top-heavy" automated testing pyramid after successful attempts of testing automation.
If we do not take any counter-measure to this situation, the test execution time will grow along with the product's development.
In order to address this challenge, **InsDog** has **Accessing Model** mechanism, which provides abstract representation of "how we (users) want to access SUT" over an accessing method, which are Web UI, API, CLI, Smartphone Applications, etc.
What you will need to do is to define an Access Model for your accessing method in a way it is abstract enough to be implemented by other accessing methods that you want to include in your test suite.
Based on the annotations you attach to the asserting functions, the tool will automatically determine which accessing method should be used and which asserting functions should be applied.

Following are the summary of the rest of the **InsDog**'s documentations.

[2-ExamplesAndTutorials](../2-ExamplesAndTutorials/index.md) collects guides about writing tests, improving the tool, introducing development tools.
[3-Design](../3-Design/index.md) Discusses **InsDog**'s design overview and important concepts.
You can find the detail API reference in [API Specification](../apidocs/index.html).

Enjoy!

