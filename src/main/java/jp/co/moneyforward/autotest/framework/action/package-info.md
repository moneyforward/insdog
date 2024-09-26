This is a package to provide "action" model of the **autotest-ca** framework.

Actions performed over the SUT are modeled as `Call` and `Act` in the framework, which creates `Action` using the concept of **actionunit** library.

```mermaid
classDiagram
    namespace ActionVisitor {
      class ActionComposer {
        <<visitor>>
        create(ActCall call) Action
        create(AssertionActCall call) Action
        create(SceneCall call) Action
      }
    }

    namespace Scenes {
        class Scene {
        }
        class Scene_Builder {
            build() Scene
        }
    }
    Scene_Builder "build" ..> Scene
    Scene_Builder ..> Call

    namespace Calls {
        class Call {
            <<element>>
            <<abstract>>
            Action toAction(ActionComposer)
        }
        class SceneCall
        class Decorator
        class RetryCall
        class AssertionCall
        class ActCall
    }
    Call <|-- SceneCall
    Call <|-- Decorator
    Decorator <|-- AssertionCall
    Decorator <|-- RetryCall
    Call <|-- ActCall
    ActCall "1" --> "1" Act: "target"
    Decorator "1" --> "1" Call: "target"

    namespace Acts {
        class Act {
            perform()
        }
        class PageAct
        class MiscAct
    }
    Act <|-- PageAct
    Act <|-- MiscAct

    namespace ActionUnit {
        class Action {
            <<interface>>
        }
        class Leaf {
        }
        class Retry {
        }
        class Misc {
        }
    }
    Action <|-- Leaf
    Action <|-- Retry
    Action <|-- Misc

    SceneCall "1" --> "1" Scene: "target"
    Scene "1" *--> "*" Call: "children"
    ActionComposer ..> Call: "toAction"
    Call ..> Action: "create"
```

In the concepts of the **autotest-ca** framework, a test consists of two elements.
**Scenes** and **Acts**.

A **Scene** is a structure of **Acts**.

**Act** can be implemented by a test programmer, typically **SDET-FW**, to model a reusable real world action such as **Click**, **Navigate**, **Screenshot**, etc.  

A **Scene** is a unit that the framework manipulates and executes.
A user programmer is expected to build a *Scene* to the execution framework in a way which it can recognize.
A call is defined for structural action such as ''leaf'', ''assertion'', ''sequential'', ''retry'', and so on.
An **Act** is a unit of a behavior, that user programmers can define as a Java code directly, and from which they build a scene.
**Calls** are classes to model an internal structure through which an **Action** (**actionunit**) tree is built.
It is held by `Scene.Builder` and translated into the tree by **ActionComposer**.

''**ActionComposer**'' and ''**Calls**'' consists a ''**Visitor**'' pattern.
A call is an 'element' in **Visitor** pattern.
''**ActionComposer**'' traverses ''**Calls**'' one by one and creates action tree to be executed.

## Data Storage Structure

A variable store is one form of a variable.
For clarity's sake, we introduce a "simple variable" to distinguish them without confusion.

```mermaid
classDiagram
    class Context
    class Variable~T~ {
        String name
        Resolver~T~ resolver
        resolve(Context) T
        name() String
    }
    class VariableStoreVariable {
        resolve(Context) VariableStore
        <T> resove(Context, String) T
    }
    class SimpleVariable~T~ {
        <<final>>
    }
    class Resolver~T~ {
        resolve(Context) T
    }
    class VariableStore {
    }
    
    VariableStore "1" --> "*" Variable
    Variable~T~ <|-- "T: VariableStore" VariableStoreVariable
    Variable~T~ <|-- SimpleVariable
    Function~Context; T~ <|-- Resolver~T~
    Resolver ..> Context
    Resolver ..> T

    VariableStoreVariable ..> Resolver
    SimpleVariable~T~ ..> "1" Resolver~T~
    note for VariableStore "This is a note."
```

In order to achieve "static scope" behavior, we have a mechanism described below:

t.b.d

```mermaid
classDiagram

  class VariableScope {
      
  }
```

```mermaid
classDiagram
    namespace visitor {
        class ActionComposer {
            <<visitor>>
        }
    }
    namespace nodes {
        class Call {
            List~String~ inputFieldNames()
            Action toAction(ActionComposer actionComposer, Map~String, Function<Context, Object>~ assignmentResolvers)*
        }
        class ActCall {
            Action toAction(...)
        }
        class TargetingCall {
            Call target
            Call target()
        }
        class SceneCall {
            final String inputStoreName
            final String outputStoreName
            inputStoreName()
            workStoreName()
            outputStoreName()
            Action toAction(...)
        }
        class RetryCall {
            Action toAction(...)
        }
        class AssertionCall {
            Action toAction(...)
        }
        class VariableStore {
            final Map~String,Object~ store
            V lookUp(String variableName)
            void store(String variableName, Object value)
            void remove(String variableName)
        }
    }
    namespace reusableUnits {
        class Scene {
            List~Call~ children()
        }
        class Act {
            void perform(...)
        }
    }
    namespace products {
        class LeafAction
        class SequentialAction
        class AssertionActions
        class RetryAction
    }

    Call <|-- ActCall
    ActCall "1" *--> "1" Act
    ActCall ..> ActionComposer

    Call <|-- TargetingCall
    TargetingCall "1" *--> "1" Call
    TargetingCall <|-- RetryCall
    TargetingCall <|-- AssertionCall
    AssertionCall ..> ActionComposer
    RetryCall ..> ActionComposer
   
    Call <|-- SceneCall
    Scene "1" --> "*" Call
    SceneCall "1" --> "variableStore" VariableStore
    SceneCall *--> "1" Scene
    SceneCall ..> ActionComposer

    ActionComposer ..> LeafAction
    ActionComposer ..> AssertionActions
    ActionComposer ..> RetryAction
    ActionComposer ..> SequentialAction
    
    LeafAction *--> Act
```

## Data Management of Scenes and Acts

A scene and its acts have inputs and outputs.
Those variables are stored in a map, which is then a variable in a "context" of **actionunit**.
This map is called "variable-space" in this document.

**Scenes** and **Acts** interacts with each others through those variables in variable-spaces.
Dependencies between **Scenes** are described by annotations defined in user programs.
Following is an example that illustrates such interactions.

```java
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;

@AutotestExecution("ongoingScene")
class ExampleAccessingModel {
  @Named
  @Export({"a", "b"})
  Scene scene1() {
    return someScene();
  }
  
  @Named
  @Export({"x", "y"})
  Scene scene2() {
    return someOtherScene();
  }
  
  @Named
  @DependsOn({"scene1", "scene2"})
  Scene ongoingScene() {
    return sceneForSomething();
  }
}
```

In this example, `ongoingScene` is designated as a method to create a scene to be executed as a test.
Since it `@DependsOn` `scene1` and `scene2`, the framework executes them, first.
As `scene1` `@Export`s `a` and `b`, they are stored in a variable-space of `scene1`.
Similarly, `x` and `y` are stored in `scene2`'s variable-space.

All those variables are copied into "Working variable-space", where a scene created by `ongoingScene` method is performed.
It may read and write variables in the space.
After the execution is finished, all the variables will be copied to its dedicated variable-space for `ongoingScene`.

**NOTE:** Instead of `@DependsOn`, you can use `@When`.
They are basically the same in terms of description capability of dependencies, just different in how they are executed.
Please check respective documentations for the differences.

Following is a diagram that illustrates this mechanism:

```mermaid
graph LR
    classDef procedure fill:#ffc0c0,color:#222222;
    classDef dataset   fill:#c0c0ff,color:#222222;
    classDef note      fill:#e0e040,stroke:808000,stroke-width:0px,color:#222222;

Fw1(Framework)-->|read|Scene1Dataset
Fw1(Framework)-->|read|Scene2Dataset

Fw1(Framework)-.->|write|Workarea

OngoingScene(OngoingScene)-->|read|Workarea
OngoingScene(OngoingScene)-.->|write|Workarea

Fw2(Framework)-->|read|Workarea
Fw2(Framework)-.->|write|OngoingSceneDataset

NoteFw1[This step is executed by the framework before ''OngoingScene'' is performed.
It copies variables from output of Scene1 and Scene2 into ''Workarea'' dataset.
] -.- Fw1
NoteOngoingScene[A scene only interacts with data in ''workarea'', which are copied from dependency scenes.] -.- OngoingScene
NoteFw2[This step is executed by the framework after ''OngoingScene'' is performed.
It copies variables from the ``Workarea` to ''OngoingScene'' dataset.
] -.- Fw2

class Framework procedure;
class Workarea,Scene1Dataset,Scene2Dataset,OngoingSceneDataset dataset;
class NoteFw1,NoteOngoingScene,NoteFw2 note;
```

Note that an **Act** can have only one input and one output, while a scene has a set of such input and output variables (variable-space).
