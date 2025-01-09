# Variable Store Mechanism

A `Scene` handles multiple `Acts`, each of which takes one input variable and ont output variable.
Therefore, as a whole a `Scene` may handle multiple variables.


```mermaid
classDiagram
    class Scene
    class Act
    namespace VariableStoreMechanism {
        class VariableStore
        class Variable
    }
    namespace Framework {
        class SceneCall
        class ActCall
    }
    namespace TestPlay {
        class Scene
        class Act
    }

    SceneCall ..> VariableStore
    SceneCall "1" --> "1" Scene
    SceneCall "1" --> "*" ActCall
    ActCall "1" --> "1" Act
    Scene "1" --> "*" Scene
    Scene "1" *--> "*" Act
    VariableStore "1" *--> "*" Variable
```

At runtime (scene-performing time), `Scene` accesses a variable store.
That variable store is prepared by the framework.
There are two sorts of variables stores: one is "output variable store" and the other is "working variable store".
During its execution, `Scene` interacts only with the latter.
A "working variable store" is composed by the framework from the exported variables of scenes depended on by the scene for which it is created.

The framework composes a "working variable store" for a scene from variables exported by the scenes which the target scene depends on.

Following is a sequence diagram that describes this procedure.

```mermaid
sequenceDiagram
    Framework ->> Dependency Variable Stores: Read Exported Variables
    Framework -->> Working Variable Store: <<create>> new Variable Store by Copying Values 
    Framework ->> Scene: Play the Scene
    Scene -->> Working Variable Store: Read Values of Variables
    Scene ->> Acts: Play the Acts
    Scene -->> Working Variable Store: Write Values of Variables
    Framework ->> Working Variable Store: Read Variables
    Framework -->> Output Variable Store: <<create>> new Output Variable Store by copying values from Working Variable Store 
```

Note that `Scene` doesn't play its child `Acts` directly but the framework takes care of it in the actual implementation.
For the simplicity's sake, we drew it in this way.