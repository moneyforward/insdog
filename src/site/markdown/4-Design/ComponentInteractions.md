# Interactions between Components of `autotest-ca`

In the context of automated testing, various actions are defined and performed over and over again.
Sometimes, as a test case itself. 
Sometimes, as a test preparation step. 
Many of them look similar to each others but slightly different from each others.
This is why we need a good mechanism to compose a testing activity from smaller and reusable elements in a programmatic way.

Not only that, we need to take more concerns into account.

One defined action can be performed in various ways, this is such a concern.
For instance, an action to deploy a certain system is used for building SUT in a given environment, but it can also be a function to be tested.
If you run it as a test, you want to collect information about the deployment in order to examine the function works as expected.
Once a test fails in its set up phase, users want to run the phase as if it were a main part of the test to collect information.
If users know that the tests wouldn't fail and want to check the system's quality state quickly, they want to skip log collections.

Also, you need to make both the test report and the test code readable without creating repetitions at the same time.
On a failure, users want to see what is going on without being forced to repeat "run->fail->fix->run->" loop.
These are challenges not seen in product code, but only in automated testing tool's context.

To achieve such required flexibilities, `autotest-ca` employs the following mechanisms and design policies.

- `Act`, `Scene`, and `ActionFactory` structure
- Action compilation pipeline
- Custom `JUnit5` extension (custom test runner) and annotation based programming model

Note that code examples and class/sequence diagrams in this page are only intended to describe the basic concepts and may be different from the final product code in their implementation details.

## Scenes, Acts, and Play

`autotest-ca` has units called `Act` or `Scene`, and they are called `ActionFactory` in general.
An `Act` is a minimal unit to define an interaction with the system.
A `Scene` consists of one or more `ActionFactories`.
An action factory can have one input and one output.
Input is read from a variable in a context to which action belongs.

Following is a diagram that models relationships between `Act`, `Scene`, and `ActionFactory`.

```mermaid
classDiagram
    ActionFactory <|-- Scene
    ActionFactory <|-- Act
    Scene "1" --> "*" ActionFactory : children
    Play "1" --> "*" Scene : baseSetUp
    Play "1" --> "*" Scene : setUp
    Play "1" --> "*" Scene : main
    Play "1" --> "*" Scene : tearDown
    Play "1" --> "*" Scene : baseTearDown
    
    <<interface>> ActionFactory
    class ActionFactory {
        Action toAction(...)
    }
    <<interface>> Scene
    class Scene {
        List~ActionFactory~ children()
    }
    <<interface>> Act
    class Act {
       R perform(T input) 
    }
    
```
:::note info
Due to a tool's limitation, generics isn't rendered same in the implementation language in the diagram and just showing overall concepts.
:::

They are modeled as Java code in a way where programmers (typically SDETs) can minimize repetitions in the test code to keep the readability and maintainability.

### Act

An `Act` models a single action executed in a test scenario.
In the context of web-UI testing, actions such as "click", "sendKey", "waitFor", "doubleClick", etc. can be modeled as an `Act`.

Following is one example of `Act` implementation: `Click`.

```java
public class Click implements Act<Page, Page> {
  private final String locatorString;
  
  public Click(String locatorString) {
    this.locatorString = locatorString;
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    page.click(locatorString);
    return page;
  }
}

```

Another example is `Value`, which is used for assigning a specified value to a "context variable".

```java
  class Value<T> implements Act<Void, T> {
    private final T value;
    
    public Value(T value) {
      this.value = value;
    }
    
    public T perform(Void value, ExecutionEnvironment executionEnvironment) {
      return this.value;
    }
  }
```

### Scene

`Scene` is another unit of reusing actions in testing context.
It can hold other `ActionFactories`(either `Act` or `Scene`) as its children.

Following is an example to show how you can structure of a `Scene` through Java language. 

```java

public class AutotestExample {
  // This example assumes `Playwright` and `Browser` instances are initialized in a `@BeforeAll` method and
  // disposed in an `@AfterAll` method.
  static Playwright playwright;
  static Browser browser;
  
  @BeforeEach
  public Scene login() {
      return new Scene.Builder().add("page", new Value(browser.newPage()))
                                //                                                                                  input field name.                                                       
                                //   output field name      
                                .add("page", new Navigate("https://ca-web-NAMESPACE.idev.test.musubu.co.in/home"), "page")
                                .add("page", new Click("a#txtbox-email"), "page")
                                // ...
                                .build();
  }
}
```

As you see, you can add arbitrary children to a new scene using a `Scene.Builder` class's methods.
`Acts` and `Scenes` under a new `Scene` can communicate with each others through "context variables" at runtime.

The `Builder#add(...)` method can take at most three parameters.
An output field name from a child `ActionFactory` to be added, the child `ActionFactory`, and an input field name to the action factory.
When the output field name or the input field name is omitted, a constant "default context variable name" will be used. 

To define a function with multiple parameters, we need "currying" mechanism, which is not supported as of now.

### Play

Play is a class to model the entire test, which consists of `setupAll`, `setUp`, `main`, `tearDown`, and `tearDownAll` action factories.
It is created by the test extension of `autotest-ca` internally and users do not need to create it by themselves in usual use cases.


## Action Compiler Pipeline

In order to modify/decorate the execution-time behavior of actions, `autotest-ca` has "Action Compiler" mechanism.

```mermaid
graph LR
    AutotestExtension
    TestClass
    ExecutionCompiler
    Play
    actionTrees
    testActionTree
    testResults
    ActionStructure
    ActionPerformer
    
    Play --> baseSetUp
    Play --> setUp
    Play --> main
    Play --> tearDown
    Play --> baseTearDown
    ExecutionCompiler -- read --> Play
    ExecutionCompiler -. write .-> ActionStructure
    ActionStructure --> beforeAll
    ActionStructure --> beforeEach
    ActionStructure --> tests
    ActionStructure --> afterEach
    ActionStructure --> afterAll
    AutotestExtension -- 1: read annotations --> TestClass
    AutotestExtension -. 2: compose a play object .-> Play
    AutotestExtension -. 3: instantiate execution compiler .-> ExecutionCompiler
    AutotestExtension -- 4: request compilation --> ExecutionCompiler
    AutotestExtension -- 5: request performing action --> ActionPerformer

    subgraph testActionTree
        beforeEach
        tests
        afterEach
    end
    subgraph actionTrees
        beforeAll
        testActionTree
        afterAll
    end
    ActionPerformer -.-> testResults
    ActionPerformer --> testActionTree
    ActionPerformer --> actionTrees
```

`Execution Compiler` compiles trees of `ActionFactories` into trees of actions, which can be performed by `ActionUnit`.
By replacing a default execution compiler with a custom one, you can control how your test is executed.

## Programming Model

Following is a code example that illustrates how a test class for `autotest-ca` will be written.

<details>
<summary>
@ExtendWith(AutotestEngine.class)
@ExecuteWith(
    compiler = ExecutionCompiler.Default.class,
    actionPerformer = ReportingActionPerformer.class)
public class AutotestExample {
</summary>

```java
@ExtendWith(AutotestEngine.class)
@ExecuteWith(compiler = ExecutionCompiler.Default.class)
public class AutotestExample implements ActionTestPerformer {
  @SetUpAll
  public Scene login() {
    return new Scene.Builder().add(SomeAction(), "varName1")
                              .add("resultVariable", new WaitFor())
                              .build();
  }
  
  @SetUpAll
  @DependsOn("login")
  public Scene connectDatasetAndRegisterAccounts() {
    return new Scene.Builder().add(SomeAction(), "varName1")
                              .add("resultVariable", new WaitFor())
                              .build();
  }
  
  @ActionTest
  public Scene registerBankAccountUnderApiPartnership_Rakuten() {
    return new Scene.Builder().add(SomeAction(), "varName1")
                              .add("resultVariable", new WaitFor())
                              .build();
  }
  
  
  @ActionTest
  @DependsOn("registerBankAccountUnderApiPartnership_Rakuten")
  public Scene unregisterBankAccount_Rakuten() {
    return new Scene.Builder().add(SomeAction(), "varName1")
                              .add("resultVariable", new WaitFor())
                              .build();
  }
  
  @AfterAll
  public Scene logout() {
    return new Scene.Builder().add(SomeAction(), "varName1")
                              .add("resultVariable", new WaitFor())
                              .build();
  }
}

```
</details>

### Class Declaration : `@ExtendWith(AutotestEngine.class)` and `@ExecuteWith(...)`

`@ExtendWith` is a built-in annotation of JUnit5.
Specify `AutotestEngine.class` to run your test under `autotest-ca` 's framework.

```java
@ExtendWith(AutotestEngine.class)
@ExecuteWith(compiler = ExecutionCompiler.Default.class)
public class AutotestExample implements ActionTestPerformer {
  // ...
}
```

`@ExecuteWith(...)` is a custom annotation to specify how the framework runs user's tests.
The `compiler` attribute specifies an "Execution Compiler" (discussed above) class.

`ActionTestPerformer` is an interface that defines how actions should be performed by default.

:::note info
This is a current limitation of `autotest-ca`.
It is preferable to be able to write a test class without implementing the `ActionTestPerformer` interface. 
:::


### Method Level Annotations: `@SetUpAll`, `@SetUpEach`, `@ActionTest`, `@TearDownEach`, and `@TearDownAll`

These annotation specifies how a method to which they are given will play a role in a test class. 

### Dependency: `@DependsOn("methodName1", "methodName2", ...)` 

This annotation specifies in what order actions produced by each method should be executed.

## References

- [JUnit5](https://junit.org/junit5/)
- [Four-Phase Test](http://xunitpatterns.com/Four%20Phase%20Test.html)
- [actionunit](https://github.com/dakusui/actionunit)




