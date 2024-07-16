package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A test dependency controlling annotation.
 *
 * This annotation is used only when the `PlanningStrategy#DEPENDENCY_BASED` is activated and usually attached to methods for test results checking.
 * This annotation can be attached to an action method (a `Scene` returning method).
 *
 * Even if the method to which this annotation is attached is not included in the explicit test scenario, it will be
 * executed as long as its target method (specified by `value()` attribute of this annotation) is a part of the main test scenario.
 *
 * The scene created by the attached method will be executed right after the target method.
 *
 * if there are following methods:
 *
 * ````java
 *
 * @Named
 * @DependsOn("login")
 * Scene targetMethod() {
 *   return scene;
 * }
 *
 * @Named
 * @When("targetMethod")
 * Scene checkMethod() {
 *    return scene;
 * }
 * ````
 *
 * And in case only `targetMethod` is specified as a part of the main test scenario, the `checkMethod` will still be executed
 * right after `targetMethod`.
 * The same dependency declaration as the `targetMethod` will be used for the `checkMethod`, too.
 *
 * Even if the target method is executed because it is depended on by others during the **beforeAll** step, the `@When` annotated method won't be executed.
 * This behavior is useful when a programmer wants to focus on a specific part of a test and save time.
 * In other words, the scenes from `@When` annotated methods are optional and controlled by the framework.
 *
 * This and `@DependsOn` annotations are used mutually exclusively.
 *
 * @see DependsOn
 * @see PlanningStrategy#DEPENDENCY_BASED
 */
@Retention(RUNTIME)
public @interface When {
  /**
   * Specifies scene names, for which the scene method references.
   *
   * @return A name of a scene.
   */
  String[] value();
}
