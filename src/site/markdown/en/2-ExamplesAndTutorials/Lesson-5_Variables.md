# Lesson 5: Variables

In order to make your **Scenes** and **Acts** interact with each others, you can define and reference "variables", which
**InsDog** provides.

An **Act** can process only one variable at a time, while **Scenes** can interact with multiple variables.

An **Act** doesn't perform by itself, but it performs only under a **Scene**.
A **Scene** has its own namespace for variables, which is called "Variable Store".
When a **Scene A**  depends on another **Scene B**, variables exported by **Scene B** are visible to **Scene A**.

Following is an example for variable handling.

```java

@AutotestExecution(defaultExecution = @Spec(
    value = "performTargetFunction",
    planExecutionWith = DEPENDENCY_BASED))
public class LessonVariables extends LessonBase {
  @Named
  @Export()
  public Scene openBasePage() {
    return Scene.begin()
                .add(openNewPage())
                .end();
  }
  
  @Named
  @Export({"page", "childPage"})
  @DependsOn("openBasePage")
  public Scene performTargetFunction() {
    return Scene.begin()
                .add(clickButton1())
                .add("childPage", openChildPage())
                .add(Scene.begin("childPage")
                          .act(screenshot())
                          .end())
                .end();
  }
  
  @Named
  @When("performTargetFunction")
  public Scene thenClickButton2() {
    return Scene.begin()
                .add(clickButton2())
                .end();
  }
  
  @Named
  @When("performTargetFunction")
  public Scene thenClickButton3() {
    return Scene.begin("childPage")
                .add(clickButton3())
                .end();
  }
}
```

Both `thenClickButton2` and `thenClickButton3` depend on `performTargetFuntion`.
`performTargetFunction` itself depends ong `openBasePage`.

`thenClickButton2` and `3` can access variables `performTargetFunction` and `openBasePage` export.
`@Export` is an annotation with which variables to be exported to others are specified.
`performTargetFunction` exports `page` and `childPage` explicitly.
`openBasePage` method has `@Export` annotation which doesn't specify any name of variable.
In this case, only a variable whose name is `page` is exported as a default[^1].
These are variables whose values they can read.

Output of this class will be like following:

```
[INFO ] [main] - LessonVariables     : value:      [o]performTargetFunction
[INFO ] [main] - LessonVariables     : value:        [o:0]output:[performTargetFunction] work:[work-id-24039137]
[INFO ] [main] - LessonVariables     : value:        [o:0]clickButton1[page]
[INFO ] [main] - LessonVariables     : value:        [o:0]openChildPage[page]
[INFO ] [main] - LessonVariables     : value:        [o:0]output:[work-id-24039137] work:[work-id-251520863]
[INFO ] [main] - LessonVariables     : value:        [o:0]screenshot[childPage]
[INFO ] [main] - LessonVariables     : value:      [o]thenClickButton2
[INFO ] [main] - LessonVariables     : value:        [o:0]output:[thenClickButton2] work:[work-id-1398260359]
[INFO ] [main] - LessonVariables     : value:        [o:0]clickButton2[page]
[INFO ] [main] - LessonVariables     : value:      [o]thenClickButton3
[INFO ] [main] - LessonVariables     : value:        [o:0]output:[thenClickButton3] work:[work-id-1531182070]
[INFO ] [main] - LessonVariables     : value:        [o:0]clickButton3[childPage]
```

## Footnotes

- [^1]: Note that, the default of `@Export` may be changed in the future.
  This default is defined assuming that users of **InsDog** mainly will use it as a helper library of web UI's
  end-to-end tests.
  However, **InsDog** can cover more than that.
  In such a context, where end-to-end UI tests and API/CLI tests are uniformly designed and implemented, the name to be
  chosen for it wouldn't be `page`, but `session` or something like that.