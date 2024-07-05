This is a package to provide "action" model of the **autotest** framework.

Actions performed over the SUT are modeled as `ActionFactory` in the framework, which creates `Action` using the concept of **actionunit** library.

Since one instance of `ActionFactory` corresponds to one `Action` instance in the model of this framework, we use the terms **action** and **action factory** interchangeably in the documentation.

```mermaid
classDiagram
    ActionFactory <|-- Act
    Act <|-- LeafAct
    Act <|-- AssertionAct
    Act <|-- PipelinedAct
    ActionFactory <|-- Scene
    Scene "1" *--> "*" ActionFactory : children
    PipelinedAct --> Act: head
    PipelinedAct --> Act: tail
    AssertionAct --> Act: parent

    class ActionFactory {
        String name()
        AssertionAct assertion(Function assertion)
    }
    class LeafAct {
        perform()
    }
    class AssertionAct {
        Act parent
    }
    class PipelinedAct {
        Act head
        Act tail
    }
```

In the concepts of the **autotest** framework, actions can be categorized into two, which are **Scenes** and **Acts**.

A **Scene** is a unit that the framework executes at the top level.
An **Act** is a unit of a behavior, that user can define as a Java code directly.


