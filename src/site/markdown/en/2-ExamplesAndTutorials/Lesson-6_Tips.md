# Tips

In this section, we discuss a few tips and tricks that might be useful.

## Accessing Model  as an Interface
Also, consider defining a set of reusable `Scene` returning methods as an `interface`.
They can be implemented by your **TestBase** class.
This approach will make sense if your application has a structure, where a larger component consists of combinations of smaller ones, and smaller ones are reused by larger ones.


## Accessing model, assertion, and test class

Ideally, "Accessing Model" represents a specification of system under test (SUT).
We expect that an accessing model for an SUT keeps growing into such a model through continuous refactorings.

In the ideal world, how would a test class look like?
Here is an example.

```java
@AutotestExecution(defaultExecution = @Spec(
                              value = "performTargetFunction1",
                  planExecutionWith = DEPENDENCY_BASED))
class SutTestClass extends SutAccessingModel {
  @Named
  @When("performTargetFunction")
  public Scene thenDatabaseRecordWasUpdated() {
    return Scene.begin().act("...").end();
  }
  
  @Named
  @When("performTargetFunction")
  public Scene thenPageWasUpdated() {
    return Scene.begin().end();
  }
}
```

As `SutAccessingModel` is a specification of the application itself, it doesn't contain any assertions.
It is a concern that should be described in the test class itself.

As you see, the class is quite coherent and readable.
It declares the target function to be tested (`value = "performTargetFunction1"`).
Preferably the number of target functions should be very small or one.
The method is defined in the class that describes the SUT's specification.

It only has methods to check the behavior of the SUT.
Not methods for functions to be tested.
Such things should go to accessing model.

## Codegen

To write a test using the **InsDog**, [`codegen`](https://playwright.dev/java/docs/codegen) is a very powerful and useful friend.
Please check it out and familiarize yourself with its way of playing DOM elements in HTMLs.
