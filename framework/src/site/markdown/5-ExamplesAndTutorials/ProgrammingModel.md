# Programming Model of "autotest-ca"

**autotest-ca** sets its own programming model, so that test writers can define high quality test classes in a natural way.
It is designed to result in compilation errors rather than runtime failures, if a test writer fails to follow preferable design as much as possible.
That said, knowing the design thought behind it will help you learn how to write tests in the model quickly.

In this document, we will walk through a working example and touch up on important elements one by one.

**NOTE:** Please note that examples in this page are all under active development as of June-July/2024.
Check the latest code and consult with the designer of this product (`ukai.hiroshi@moneyforward.co.jp`), when you start working on writing tests, although basic concepts will not be changed.

## Walking through an example

Following is the first Test Class of **autotest-ca**. 

```java
/**
 * This test assumes the account returned by the profile is clean.
 * That is:
 *
 * - it can log in to the SUT with its password
 * - it doesn't have any connected banks.
 *
 * Also, the account specified by `ExecutionProfile#userEmail` should be belonging to 
 * a company named "スペシャルサンドボックス合同会社 (法人)".
 *
 */
@Tag("bank")                                                          // (1)
@Tag("smoke")
@AutotestExecution(                                                   // (2)
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connectBank", "disconnectBank", "logout"},
        afterEach = {"screenshot"},
        afterAll = {"close"}))
public class BankConnectingTest extends CawebAccessingModel /* (3) */ {
}
```

In the design concept of **autotest-ca**, a test class is equivalent to a test scenario.

* **(1):** "Tag", with which you can filter test to be executed by CLI.
See [Execution](Execution.md) for its usage at runtime.
* **(2):** Default Execution Directive.


### Access Model

**Access Model** is an abstraction of steps performed in test scenarios.
It consists of **Scenes**, each of which represents a step in a test scenario.
However, scenes are defined with their dependencies.

Static methods annotated with `@Named` are considered scenes that can be a part of test execution directive.
By `@DependsOn` annotation, you can specify which **Scene** needs to be performed before the scene.
A **Scene** method is supposed to return `Scene` object, which can be built by `Scene.Builder`.

Note that how to build a scene object using the builder is omitted in this example for the simplicity's sake.

```java
public class CawebAccessingModel implements AutotestRunner {
  
  @Named
  public static Scene open() {
    return new Scene.Builder("NONE").build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene login() {
    return new Scene.Builder("login").build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene connectBank() {
    return new Scene.Builder("page").build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene disconnectBank() {
    return new Scene.Builder("page").build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "login", fieldNameInSourceScene = "page"))
  public static Scene logout() {
    return new Scene.Builder("page").build();
  }
  
  @Named
  @DependsOn(
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page"))
  public static Scene screenshot() {
    return new Scene.Builder("page").build();
  }
  
  @Named
  @DependsOn({
      @Parameter(name = "browser", sourceSceneName = "open", fieldNameInSourceScene = "browser"),
      @Parameter(name = "window", sourceSceneName = "open", fieldNameInSourceScene = "window"),
      @Parameter(name = "page", sourceSceneName = "open", fieldNameInSourceScene = "page")}
  )
  public static Scene close() {
    return new Scene.Builder("page").build();
  }
}
```
**NOTE:** `@Named` annotation can have a string `value`.
The value will be treated as a name of the scene method with which **Execution Directive** references.
By default, the name of the method will be used.
This means, overloading scene methods require explicit `@Named` value without collisions.


**NOTE:** Currently the **Access Model** needs to be extended by test model, however test execution and access model are essentially separate concerns.
For instance, you may want to execute the same test scenario using a different access model than Web UI, let's say, public API.
In such a situation, you want to change the access model at execution time through runtime CLI parameter.
This enhancement will be made in the future.

**NOTE:** It is being considered to separate the dependency declaration and variable definitions.
That is:
```java
  @Named
  @DependsOn("open")
  @Import(value="page", from="page@open")
  public static Scene screenshot() {
    return new Scene.Builder("screenshot").build();
  }
```
This will allow us to declare `@DependsOn` in an abstract **Access Model**, which is common to web-UI and API, for instance, while we can declare `@Import` in the concrete access models, so that they can access different variables accordingly.


### Scene and Act

A **scene** consists of one or more **acts**.
A **scene** is a minimum unit that a user can specify its execution at runtime.
An **act** is a minimum unit that test programmers can reuse.
It is suggested to create reusable **acts** (functions to create **acts**, classes that represents **acts**) so that code duplication can become minimum.

```java
  public static Scene disconnectBank() {
    return new Scene.Builder("page")
        // (1)
        .add(new Navigate(EXECUTION_PROFILE.accountsUrl()))
        // (2)
        .add(new PageAct("金融機関を削除する") {            
          @Override
          protected void action(Page page, ExecutionEnvironment executionEnvironment) {
            page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName("\uF142")).locator("a").click();
            page.onceDialog(Dialog::accept);
            page.getByTestId("ca-client-test-account-dropdown-menu-delete-button")
                .click();
          }
        })
        .build();
  }
```

You can write the implementation of the `action` method with a help of [`codegen`](https://playwright.dev/java/docs/codegen) tool of **Playwright**.


* **(1):** An example of "preset" acts.
You can find pre-defined acts and utilities (classes and functions) to create acts under `jp.co.moneyforward.autotest.actions.web` package.
Please consider using them to define your `Scene`.
* **(2):** A **PageAct** allows direct access to the `Page` object of **Playwright**.
By typing `new PageAct("Some descriptive name") {[ENTER]`, your IDE will generate the template, and you can start writing its definition.
In the body of the `action` method, you can just write ordinary **Playwright-Java** code.
ChatGPT's Playwright-Java mode is your friend, when you need to translate Selenium-based locators into Playwright's flavor.
It does pretty much a good job.

If you use the pre-defined acts and utilities more, the log file will be more informative with minimum noise, automatically.
However, it has its learning curve, so, it is suggested to start from the `new PageAct` approach first until you feel you get familiarized.

Still, keeping each `PageAct` object as small as possible is very important.
Also, try to find reusable elements in your `PageAct` and extract reusable methods for others!

### Execution Directive

```java
@AutotestExecution(                                                   
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connectBank", "disconnectBank", "logout"},
        afterEach = {"screenshot"},
        afterAll = {"close"}))
public class Example {}
```

This portion defines how this test class executes which scenes.

In each attribute (`beforeAll`, `beforeEach`, `value`, `afterEach`, and `afterAll`) , you can specify the scene names (`@Named#value` or name of a scene method).
They are executed in this order:

* `beforeAll[0..*]`
  * `beforeEach[0..*]`
      * `value[0]`
  * `afterEach[0..*]` 
  * `beforeEach[0..*]`
      * `value[1]`
  * `afterEach[0..*]`
  * ...
  * `beforeEach[0..*]`
      * `value[*]`
  * `afterEach[0..*]`
* `afterAll[0..*]`

Note that only scenes specified in `value` are considered true *tests*.

In general, steps to set up a test fixture should go to `beforeAll`, information collection for a failure analysis should go to `afterEach`, and clean up steps should go to `afterAll`.
In case information collection needs a preparation before executing a test, you can define it in `beforeEach` step.

## Where to define **Scene** methods? In a test class, or in access model?

If a **Scene** method is obviously used only by the test you are about to write, it should be in the test class.
If it is obviously used in many test classes, it should go to `CaWebAccessModel` class.
But reality is always somewhere in between.

For the time being let's take this policy.
If it is not obviously used by many tests, write it in your test class.
Once the second user appears, move it to an appropriate class.

Don't worry, they are all static methods, which you can easily move around.

