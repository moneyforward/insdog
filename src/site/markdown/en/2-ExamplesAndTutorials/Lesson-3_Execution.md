# Lesson 2: Execution

In **InsDog**, execution of a test can be controlled by `@AutotestExecution` annotation attached to a test class and by
its overriding value given from the CLI.

## The Basic: The "Passthrough" mode

Following is the example of it.

```java

@AutotestExecution(
    defaultExecution = @Spec(
        value = {
            "aSceneMethod"
        },
        planExecutionWith = PASSTHROUGH
    ))
public class Lesson extends LessonBase {
}
```

But there are hidden attributes: `beforeAll`, `beforeEach`, `afterEach`, and `afterAll`.

If we do not omit the default values, it will be like following:

```java

@AutotestExecution(
    defaultExecution = @Spec(
        beforeAll = {},
        beforeEach = {},
        value = {
            "aSceneMethod"
        },
        afterEach = {},
        afterAll = {},
        planExecutionWith = PASSTHROUGH
    ))
public class Lesson extends LessonBase {
}
```

This executes a scene object returned by `aSceneMethod` as a test method.

Basically, what they do is what you imagine by analogy from **JUnit** test annotations.

* `beforeAll`: Executes scenes returned by specified methods before any tests and their `beforeEach` step.
  This happens only once for a test class.
  This stage suits for preparation steps of a test class.
* `beforeEach`: Executes scenes returned by specified methods before each east.
  This stage suits for taking a screenshot to compare it with the one taken in `beforeEach`.
* `afterEach`: Executes scenes returned by specified methods after each east.
  This stage also suits for taking a screen for debugging purpose on a test failure.
* `afterAll`: Executes scenes returned by specified methods after all tests and their `afterEach`.
  This happens only once for a test class.
  This stage suits for closing steps of a test class

If the SUT is not a GUI, it will be a good idea to collect data and log files in `afterEach` stage.
In case, you want to download only log slices that contain events after the test starts, you can also implement a scene
to record lengths of log files and put it in `beforeEach` stage.

Here is one basic rule.
What you explicitly specified is executed by the framework.
In the passthrough mode, what you wrote is what should be executed and that's it.

In the next section, we will review the "Dependency-based" mode.

## In Practice: The "Dependency-based" mode

In practice, actions written in a test class may have dependencies.
One may be a precondition of another, as such.

When we model SUT's specification as code, we frequently find non-simple dependencies between actions performed as a
part of set up phase.

In such a situation, it will be tedious and error-prone to describe the same things over and over again.

If we define a scene method which assumes `setUpMethod` is executed beforehand as follows:

```java

@Named
public Scene sceneMethod() {
  return Scene.begin()
              .act(new Let<>("InsDog"))
              .act(new Sink<>(System.out::println))
              .end();
}
```

It is not a good idea to have users write `beforeAll` everytime they use the method:

```java

@AutotestExecution(
    defaultExecution = @Spec(
        beforeAll = "setUpMethod",
        value = {
            "sceneMethod"
        },
        planExecutionWith = PASSTHROUGH
    ))
public class Lesson extends LessonBase {
}
```

Instead, such dependency should be automatically resolved.
The approach **InsDog** takes is to let the user method be annotated with `@DependsOn`.

```java

@AutotestExecution(defaultExecution = @Spec(
    value = "sceneMethod",
    planExecutionWith = DEPENDENCY_BASED))
public class Lesson extends LessonBase {
  @Named
  public Scene setUpMethod() {
    return Scene.begin()
                .act(new Let<>("InsDog"))
                .act(new Sink<>(System.out::println))
                .end();
  }
  
  @DependsOn("setUpMethod")
  @Named
  public Scene sceneMethod() {
    return Scene.begin()
                .act(new Let<>("InsDog"))
                .act(new Sink<>(System.out::println))
                .end();
  }
}
```

This may seem a trivial thing, however, consider that the `setUpMethod` may not be the only dependency of `sceneMethod`
and the `sceneMethod` itself may have its own dependencies.
Without having this sort of dependency resolution mechanism, your test suite will become easily unmanageable.
We will have more on this in [Lesson-3: Dependency](Lesson-4_Dependency) section.