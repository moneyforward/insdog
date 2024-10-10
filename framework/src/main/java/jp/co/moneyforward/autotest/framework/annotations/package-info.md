This package hols annotations defined by the **autotest** framework.

## `@AutotestExecution`: Test Class's Fallback Behaviors

This annotation specifies the fallback (default) behaviors of your test class.
The behavior can be overridden through CLI parameters.

**NOTE:** Currently, there is no way to override the default behavior from higher level.
You need to modify the code directly and re-compile it for now.

## `@Named`: Identity

This annotation specifies a name of an entity, typically a **Scene** providing method.
In case, you don't give a `value` of it, the framework considers the name of the entity, i.e. a name of the method if it is a method, is the name of it.

Note that only with the name, the **autotest** framework-core identifies an entity.
Meaning that if you don't give this annotation, it simply doesn't recognize a method you create at all.

**NOTE:** Currently, the framework doesn't do validations on your class at all.
Be careful.

## `@Export`: Exporting Variables for Other Scenes

This annotation can be attached to a **Scene** providing method.
Its value specifies the variable names that can be used by other **Scene** providing methods, which depend on the attached **Scene** providing method.

## `@DependsOn`, `@When`, and `@ClosedBy`

If you choose a `PlanningSteatedy.DEPENDENCY_BASED` for `@AutotextExecution.Spec#planExecutionWith`, the **autotest** framework respects the annotations `@DependsOn`, `@When`, and `@ClosedBy` attached to the **Scene** providing methods.

For more details of their semantics, check respective documents of annotations and the `PlanningStrategy`.

## References

@see PlanningStrategy

