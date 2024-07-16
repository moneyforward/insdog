package jp.co.moneyforward.autotest.framework.testengine;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.ClosedBy;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.When;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static jp.co.moneyforward.autotest.framework.testengine.AutotestEngineUtils.mergeListsByInsertingMissedOnes;

/**
 * Used with `Spec#planExecutionWith`.
 * Specifies how actual execution of actions (given by `Spec#{beforeAll,beforeEach,value,afterEach,afterAll}`) is planned.
 *
 * @see AutotestExecution.Spec#planExecutionWith()
 */
public enum PlanningStrategy {
  /**
   * Actions will be executed as they are specified.
   * That is, actions specified for `beforeAll` will be executed in `beforeAll` test execution phase JUnit, as such.
   */
  PASSTHROUGH {
    @Override
    AutotestEngine.ExecutionPlan planExecution(AutotestExecution.Spec executionSpec, Map<String, List<String>> sceneCallGraph, Map<String, String> closers, Map<String, List<String>> assertions) {
      return new AutotestEngine.ExecutionPlan(
          asList(executionSpec.beforeAll()),
          asList(executionSpec.beforeEach()),
          asList(executionSpec.value()),
          asList(executionSpec.afterEach()),
          asList(executionSpec.afterAll()));
    }
  },
  /**
   * Actions will be executed based on dependency resolution.
   * That is, ones specified in `value` will be considered "true" tests and actions depended on by them will be considered "arranging" (or "set up") actions.
   * The framework ensures ones not explicitly specified in `value` but depended on by one in the step to be included in `beforeAll` step.
   *
   * If an action is annotated with `@ClosedBy` and it is in `beforeAll` step, the referenced action will be included in `afterAll` step.
   * This addition is done in the reverse order, where actions which have `@ClosedBy` are found.
   * At this addition, the target action (an action by specified by `@ClosedBy` annotation) should be wrapped by the **actionunit** 's `When` action, so that it will be performed the original action has succeeded.
   * (Without this mechanism, a releasing action will be executed even if a resource to be released is not allocated because of a failure)
   *
   * Similarly, if an action in `beforeEach` has `@ClosedBy`, the referenced action will be included in `afterEach` step.
   *
   * Suppose, `open` has the `@ClosedBy("close")`.
   * If it is in `beforeAll`, `close` will be included in `afterAll`.
   * If `close` is already in `afterAll`, it will not be added again.
   * If it is in `beforeEach`, `close` will be included in `afterEach`.
   *
   * This feature is useful to ensure allocated resources are released.
   *
   * @see ClosedBy
   * @see When
   * @see DependsOn
   */
  DEPENDENCY_BASED {
    @Override
    AutotestEngine.ExecutionPlan planExecution(AutotestExecution.Spec executionSpec, Map<String, List<String>> sceneCallGraph, Map<String, String> closers, Map<String, List<String>> assertions) {
      List<String> explicitlySpecified = List.of(executionSpec.value());
      List<String> sorted = AutotestEngineUtils.topologicalSort(explicitlySpecified, sceneCallGraph);
      String firstSpecified = sorted.stream()
                                    .filter(explicitlySpecified::contains)
                                    .findFirst()
                                    .orElseThrow(NoSuchElementException::new);
      List<String> beforeAll = sorted.subList(0, sorted.indexOf(firstSpecified));
      List<String> main = sorted.subList(sorted.indexOf(firstSpecified), sorted.size());
      return includeAssertions(ensureClosersAreIncluded(new AutotestEngine.ExecutionPlan(
          AutotestEngineUtils.mergeListsByAppendingMissedOnes(List.of(executionSpec.beforeAll()), beforeAll),
          asList(executionSpec.beforeEach()),
          main,
          asList(executionSpec.afterEach()),
          asList(executionSpec.afterAll())), closers), assertions);
    }
    
    /**
     *
     * @param executionPlan An original execution plan
     * @param closers A mapping from a scene in beforeEach or beforeAll to closer scenes.
     * @return Updated execution plan.
     */
    private static AutotestEngine.ExecutionPlan ensureClosersAreIncluded(AutotestEngine.ExecutionPlan executionPlan, Map<String, String> closers) {
      List<String> afterAll = new LinkedList<>();
      executionPlan.beforeAll()
                   .stream()
                   .filter(closers::containsKey)
                   .map(closers::get)
                   .forEach(afterAll::addFirst);
      List<String> afterEach = new LinkedList<>();
      executionPlan.beforeEach()
                   .stream()
                   .filter(closers::containsKey)
                   .map(closers::get)
                   .forEach(afterEach::addFirst);
      return new AutotestEngine.ExecutionPlan(
          executionPlan.beforeAll(),
          executionPlan.beforeEach(),
          executionPlan.value(),
          mergeListsByInsertingMissedOnes(executionPlan.afterEach(), afterEach),
          mergeListsByInsertingMissedOnes(executionPlan.afterAll(), afterAll)
      );
    }
    
    private static AutotestEngine.ExecutionPlan includeAssertions(AutotestEngine.ExecutionPlan executionPlan, Map<String, List<String>> assertions) {
      return new AutotestEngine.ExecutionPlan(
          executionPlan.beforeAll(),
          executionPlan.beforeEach(),
          includeAssertions(executionPlan.value(), assertions),
          executionPlan.afterEach(),
          executionPlan.afterAll()
      );
    }
    
    private static List<String> includeAssertions(List<String> value, Map<String, List<String>> assertions) {
      return value.stream()
                  .flatMap(s -> Stream.concat(Stream.of(s),
                                              assertions.containsKey(s) ? assertions.get(s).stream()
                                                                        : Stream.empty()))
                  .toList();
    }
  };
  
  abstract AutotestEngine.ExecutionPlan planExecution(AutotestExecution.Spec executionSpec, Map<String, List<String>> sceneCallGraph, Map<String, String> closers, Map<String, List<String>> assertions);
}
