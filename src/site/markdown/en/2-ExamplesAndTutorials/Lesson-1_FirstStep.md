# Lesson 1: The First Test Class with Inspektor Dog (a.k.a. InsDog)

Here is the first "test class" that just prints "InsDog" to the standard output.

```java
/**
 * The First Lesson:
 * - Create the first test.
 */
@AutotestExecution(
    defaultExecution = @Spec(
        value = {
            "aSceneMethod"
        },
        planExecutionWith = DEPENDENCY_BASED
    ))
public class Lesson1 extends LessonBase {
  @Named
  public Scene aSceneMethod() {
    return Scene.begin()
                .act(new Let<>("InsDog"))
                .act(new Sink<>(System.out::println))
                .end();
  }
}
```

If you run this class from your IDE, it will print something like following.

```text
InsDog
[INFO ] [2024/12/24 16:14:10.726] [main] - +-[o:0]BEGIN[aSceneMethod]@[work-id-1979055448]
[INFO ] [2024/12/24 16:14:10.726] [main] - |-+-[o:0]let[InsDog][page]
[INFO ] [2024/12/24 16:14:10.727] [main] - | +-[o:0]sink[page]
[INFO ] [2024/12/24 16:14:10.727] [main] - +-[o:0]END[aSceneMethod]
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      [o]aSceneMethod
...
[INFO ] [2024/12/24 16:14:10.701] [main] - - beforeEach:     []
[INFO ] [2024/12/24 16:14:10.701] [main] - - value:          [aSceneMethod]
[INFO ] [2024/12/24 16:14:10.701] [main] - - afterEach:      []
[INFO ] [2024/12/24 16:14:10.701] [main] - - afterAll:       []
[INFO ] [2024/12/24 16:14:10.701] [main] - ----
```

What does each line in this example code and output mean?
We will be walking through it in this lesson.
Enjoy!

## The Code

Let's begin with the class declaration.
There is an annotation and class declaration.

```java
@AutotestExecution(
    defaultExecution = @Spec(
        value = {
            "aSceneMethod"
        },
        planExecutionWith = DEPENDENCY_BASED
    ))
public class Lesson1 extends LessonBase {
}
```

The `@AutotestExecution` annotation tells how the test class should be executed.
Currently, the annotation only defines the "default" behavior of the test class.
Before digging into its full specification, let's discuss what each line in the example means.

```text
    defaultExecution = @Spec(
        value = {
            "aSceneMethod"
        },
        planExecutionWith = DEPENDENCY_BASED
    ))
```

The `@Spec` annotation holds the information about what methods in the class should be executed "test methods".
In this case, a method named `aSceneMethod` is it.
The next line: `planExecutionWith = DEPENDENCY_BASED`, request the framework that "dependencies" of methods need to be resolved.
In case this execution mode is specified, methods depended on by  `aSceneMethod`, it 
is ensured that they will be executed beforehand.

If you don't want the dependency resolution to happen, you can specify `planExecutionWith = PASSTHROUGH`[^1].
More details are discussed in [Lesson 2: Execution](Lesson-3_Execution)

The class declaration (`Lesson1 extends LessonBase`) makes the class inherit `LessonBase`, which is defined for this lesson project.
Based on the situations and situations of your project you can define the base class.

Now, how the test method `aSceneMethod` looks like?

```text
  @Named
  public Scene aSceneMethod() {
    return Scene.begin()
                .act(new Let<>("InsDog"))
                .act(new Sink<>(System.out::println))
                .end();
  }
```

The conventions here are:

* Annotated with `@Named`
* It is public
* No parameter
* It returns a `Scene`

Note that these restrictions may be relaxed in future versions.

To construct a `Scene` object, you can use a builder of it.
Such as `new Scene.Builder()` and `Scene#build()`.
But it is recommended to use `Scene.begin()` and `Scene#end()` methods for code readability's sake, rather than using bare builder constructor and build method.

Similarly, to add a child to `Scene`, there are choices whether we use `add(...)` methods or methods with more meaningful names (`act(...)` and `scene(...)`).
The answer is, prefer `act(...)` and `scene(...)` over `add(...)` unless there is a special reason.
Because they will make your code more readable.

## The Log and The Output

Now, time to dig in to the output of **InsDog**-based tests.

```text
InsDog
[INFO ] [2024/12/24 16:14:10.726] [main] - +-[o:0]BEGIN[aSceneMethod]@[work-id-1979055448]
[INFO ] [2024/12/24 16:14:10.726] [main] - |-+-[o:0]let[InsDog][page]
[INFO ] [2024/12/24 16:14:10.727] [main] - | +-[o:0]sink[page]
[INFO ] [2024/12/24 16:14:10.727] [main] - +-[o:0]END[aSceneMethod]
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      [o]aSceneMethod
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      +-[o:0]BEGIN[aSceneMethod]@[work-id-1979055448]
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      |-+-[o:0]let[InsDog][page]
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      | +-[o:0]sink[page]
[INFO ] [2024/12/24 16:14:10.727] [main] - Lesson1             : value:      +-[o:0]END[aSceneMethod]
[INFO ] [2024/12/24 16:14:10.699] [main] - Running tests in: jp.co.moneyforward.autotest.lessons.Lesson1
[INFO ] [2024/12/24 16:14:10.700] [main] - ----
[INFO ] [2024/12/24 16:14:10.701] [main] - Execution plan is as follows:
[INFO ] [2024/12/24 16:14:10.701] [main] - - beforeAll:      []
[INFO ] [2024/12/24 16:14:10.701] [main] - - beforeEach:     []
[INFO ] [2024/12/24 16:14:10.701] [main] - - value:          [aSceneMethod]
[INFO ] [2024/12/24 16:14:10.701] [main] - - afterEach:      []
[INFO ] [2024/12/24 16:14:10.701] [main] - - afterAll:       []
[INFO ] [2024/12/24 16:14:10.701] [main] - ----
```

The first line is:

```text
InsDog
```

This is from the line in the code: `System.out::println`.

```text
  @Named
  public Scene aSceneMethod() {
    return Scene.begin()
                .act(new Let<>("InsDog"))             // <--- (1)
                .act(new Sink<>(System.out::println)) // <--- (2)
                .end();
  }
```

The line (1) assigns a result of `new Let<>("InsDog")` to a default output variable[^2].
The line (2) processes the current value of the default output variable with the method reference `System.out::println`.
As the name suggests `Sink` doesn't have an output.
This means the default output variable will be cleared with `null`.
Thus, the string `InsDog` is printed to the standard output.


The next part:

```text
[INFO ] [2024/12/24 16:14:10.726] [main] - +-[o:0]BEGIN[aSceneMethod]@[work-id-1979055448]
[INFO ] [2024/12/24 16:14:10.726] [main] - |-+-[o:0]let[InsDog][page]
[INFO ] [2024/12/24 16:14:10.727] [main] - | +-[o:0]sink[page]
[INFO ] [2024/12/24 16:14:10.727] [main] - +-[o:0]END[aSceneMethod]
```

This is what we call an "action tree" or an "action tree report".
At the end of a top level scene, which corresponds to a test method, this information is printed to the log.

If you write a proper test, just by take a glance at the tree, you will be able to understand the overview about what is going on.

```text
                                              +--------------------------- Symbol for success ("o" for ok) 
                                              | +------------------------- duration spent for this step [sec]
                                              | |       +----------------- output variable store name
                                              | |       |              +-- working variable store name 
                                              | |       |              |
                                              V V       V              V
[INFO ] [2024/12/24 16:14:10.726] [main] - +-[o:0]BEGIN[aSceneMethod]@[work-id-1979055448]
```

Depending on the context what will be printed will be a bit different.

```text

                                                    +---------------- A name of an act.
                                                    |            +--- A variable name the value assigned to
                                                    |            | 
                                                    V            V
[INFO ] [2024/12/24 16:14:10.726] [main] - |-+-[o:0]let[InsDog][page]
```

When an error happens after 10 seconds the line will look like as follows:

```text
                                                +------- Symbol for error ("E" for error)
                                                | 
                                                V 
[INFO ] [2024/12/24 16:14:10.726] [main] - |-+-[E:10]let[InsDog][page]
```

```
## Footnotes

* [^1] Currently `PASSTHROUGH` is the default.
However, it will be changed to `DEPENDENCY_BASED`.
It is encouraged to specify the value of `planExecutionWith` explicitly, always.
* [^2] You can explicitly specify the variable name by doing `add("newVar", new Let<>("InsDog"))` instead of `act(new Let...)`, when necessary.  
* 