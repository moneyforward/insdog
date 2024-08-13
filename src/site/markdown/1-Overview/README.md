# Overview

The **autotest-ca** is an automated end-to-end test tool for **caweb**, one of the products that **MoneyForward** is most proud of.
It has the following design concepts.

1. **Written in Java, not having a separate DSL:**
It is written in Java in order to allow test-writer to access the most matured knowledge pool among others.
This will also allow us to access larger hiring pool than the predecessor's ([駄犬くん](https://github.com/moneyforward/ca_web_e2e_test_d/tree/main/script/daken_kun) was written in Ruby, and it is becoming more and more difficult to find a fine programmer in the language).  
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
It will be called an action, and in **autotest-ca**, it will be called `Act`, `Scene`, or `Action`, depending on the context and purposes they fulfil.
4. **Application Neutrality:**
Although it is developed for **caweb**, it is designed to be able to work for other applications from the day one.
As you see in the package structure, we have a package `jp.co.moneyforward.autotest.ca_web`.
Outside this package, nothing is depending on the application's spec.
We can always add a new package for a new application.
5. **Separation of Tests, Accessing model:**
Similar to the neutrality for applications, it has a concept of **Accessing Model**.
It is a common concern to have a "top-heavy" automated testing pyramid after successful attempts of testing automation.
If we do not take any counter-measure to this situation, the test execution time will grow along with the product's development.
In order to address this challenge, **autotest-ca** has **Accessing Model** mechanism, which provides abstract representation of "how we (users) want to access SUT" over an accessing method, which are Web UI, API, CLI, Smartphone Applications, etc.
What you will need to do is to define an Access Model for your accessing method in a way it is abstract enough to be implemented by other accessing methods that you want to include in your test suite.
Based on the annotations you attach to the asserting functions, the tool will automatically determine which accessing method should be used and which asserting functions should be applied.

Following are the summary of the rest of the **autotest-ca**'s documentations.

[2-Requirements](../2-Requirements/index.md) Discusses requirement items for **autotest-ca**'s initial version.
You can find the detail API reference in [3-APISpecification](../3-APISpecification/index.md).
The software architecture of **autotest-ca** is discussed in [4-Design](../4-Design/index.md), which is focusing on tool's internals.
On the contrary, [5-Integration](../5-Integration/index.md) is focusing on how it interacts with external entities, such as SUT (**caweb**),  CI system (GitHub actions), etc. 
[6-ExamplesAndTutorials](../6-ExamplesAndTutorials/index.md) collects guides about writing tests, improving the tool, introducing development tools. 
Check [7-TroublehootingAndSupport](../7-TroubleshootingAndSupport/index.md), when you need a help.
You can find release notes under [8-ChangeLog](../8-ChangeLog/index.md).  
In [X-Appendix](../X-Appendix/index.md), detail and technical information and resources will be collected.

Enjoy!

