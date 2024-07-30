package jp.co.moneyforward.autotest.ut.framework.engine;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine.ExecutionPlan;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlanningStrategyTest {
  @Test
  void givenEmptyPlanSpec_whenPlanExecution_thenNoSuchElementThrown() {
    assertThrows(
        NoSuchElementException.class, () -> {
          DEPENDENCY_BASED.planExecution(createExecutionSpec(
                                             List.of(),
                                             List.of(),
                                             List.of(),
                                             List.of(),
                                             List.of()),
                                         new HashMap<>(),
                                         new HashMap<>());
        });
  }
  
  @Test
  void givenNoDependencyPlan_whenPlanExecution_thenPassthrough() {
    ExecutionPlan executionPlan = DEPENDENCY_BASED.planExecution(createExecutionSpec(
                                                                     List.of("stepBeforeAll"),
                                                                     List.of("stepBeforeEach"),
                                                                     List.of("test1", "test2"),
                                                                     List.of("stepAfterEach"),
                                                                     List.of("stepAfterAll")
                                                                 ),
                                                                 new HashMap<>() {{
                                                                   put("test1", List.of());
                                                                   put("test2", List.of());
                                                                 }},
                                                                 new HashMap<>());
    
    assertAll(value(executionPlan).function(ExecutionPlan::beforeAll).asList().toBe().equalTo(List.of("stepBeforeAll")),
              value(executionPlan).function(ExecutionPlan::beforeEach).asList().toBe().equalTo(List.of("stepBeforeEach")),
              value(executionPlan).function(ExecutionPlan::value).asList().toBe().equalTo(List.of("test1", "test2")),
              value(executionPlan).function(ExecutionPlan::afterEach).asList().toBe().equalTo(List.of("stepAfterEach")),
              value(executionPlan).function(ExecutionPlan::afterAll).asList().toBe().equalTo(List.of("stepAfterAll")));
  }
  
  @Test
  void givenPlanWithSingleDependencyOnBeforeAll_whenPlanExecution_thenDependencyResolved() {
    ExecutionPlan executionPlan = DEPENDENCY_BASED.planExecution(createExecutionSpec(
                                                                     List.of("stepBeforeAll"),
                                                                     List.of("stepBeforeEach"),
                                                                     List.of("test2"),
                                                                     List.of("stepAfterEach"),
                                                                     List.of("stepAfterAll")
                                                                 ),
                                                                 new HashMap<>() {{
                                                                   put("beforeAll", List.of());
                                                                   put("beforeEach", List.of());
                                                                   put("test1", List.of());
                                                                   put("test2", List.of("test1"));
                                                                   put("afterEach", List.of());
                                                                   put("afterAll", List.of());
                                                                 }},
                                                                 new HashMap<>());
    
    assertAll(value(executionPlan).function(ExecutionPlan::beforeAll).asList().toBe().equalTo(List.of("stepBeforeAll", "test1")),
              value(executionPlan).function(ExecutionPlan::beforeEach).asList().toBe().equalTo(List.of("stepBeforeEach")),
              value(executionPlan).function(ExecutionPlan::value).asList().toBe().equalTo(List.of("test2")),
              value(executionPlan).function(ExecutionPlan::afterEach).asList().toBe().equalTo(List.of("stepAfterEach")),
              value(executionPlan).function(ExecutionPlan::afterAll).asList().toBe().equalTo(List.of("stepAfterAll")));
  }
  
  @Test
  void givenPlanWithSingleDependencyOnBeforeEach_whenPlanExecution_thenDependencyResolved() {
    ExecutionPlan executionPlan = DEPENDENCY_BASED.planExecution(createExecutionSpec(
                                                                     List.of("stepBeforeAll", "stepBeforeEach"),
                                                                     List.of("stepBeforeEach"),
                                                                     List.of("test2"),
                                                                     List.of("stepAfterEach"),
                                                                     List.of("stepAfterAll")
                                                                 ),
                                                                 new HashMap<>() {{
                                                                   put("stepBeforeEach", List.of());
                                                                   put("test2", List.of("stepBeforeEach"));
                                                                 }},
                                                                 new HashMap<>());
    
    assertAll(value(executionPlan).function(ExecutionPlan::beforeAll).asList().toBe().equalTo(List.of("stepBeforeAll", "stepBeforeEach")),
              value(executionPlan).function(ExecutionPlan::beforeEach).asList().toBe().equalTo(List.of("stepBeforeEach")),
              value(executionPlan).function(ExecutionPlan::value).asList().toBe().equalTo(List.of("test2")),
              value(executionPlan).function(ExecutionPlan::afterEach).asList().toBe().equalTo(List.of("stepAfterEach")),
              value(executionPlan).function(ExecutionPlan::afterAll).asList().toBe().equalTo(List.of("stepAfterAll")));
  }
  
  @Test
  void givenPlanWithClosers_whenPlanExecution_thenAssertionsAreAdded() {
    ExecutionPlan executionPlan = DEPENDENCY_BASED.planExecution(createExecutionSpec(
                                                                     List.of("stepBeforeAll"),
                                                                     List.of("stepBeforeEach"),
                                                                     List.of("test1", "test2"),
                                                                     List.of("stepAfterEach"),
                                                                     List.of("stepAfterAll")
                                                                 ),
                                                                 new HashMap<>() {{
                                                                   put("test1", List.of());
                                                                   put("test2", List.of());
                                                                 }},
                                                                 new HashMap<>() {{
                                                                   put("test1", List.of("assertion1a", "assertion1b"));
                                                                   put("test2", List.of("assertion2a", "assertion2b"));
                                                                 }});
    
    assertAll(value(executionPlan).function(ExecutionPlan::beforeAll).asList().toBe().equalTo(List.of("stepBeforeAll")),
              value(executionPlan).function(ExecutionPlan::beforeEach).asList().toBe().equalTo(List.of("stepBeforeEach")),
              value(executionPlan).function(ExecutionPlan::value).asList().toBe().equalTo(List.of("test1", "assertion1a", "assertion1b", "test2", "assertion2a", "assertion2b")),
              value(executionPlan).function(ExecutionPlan::afterEach).asList().toBe().equalTo(List.of("stepAfterEach")),
              value(executionPlan).function(ExecutionPlan::afterAll).asList().toBe().equalTo(List.of("stepAfterAll")));
  }
  
  private static AutotestExecution.Spec createExecutionSpec(List<String> beforeAll,
                                                            List<String> beforeEach,
                                                            List<String> value,
                                                            List<String> afterEach,
                                                            List<String> afterAll
  ) {
    return new AutotestExecution.Spec() {
      
      @Override
      public Class<? extends Annotation> annotationType() {
        return AutotestExecution.Spec.class;
      }
      
      @Override
      public String[] beforeAll() {
        return beforeAll.toArray(new String[0]);
      }
      
      @Override
      public String[] beforeEach() {
        return beforeEach.toArray(new String[0]);
      }
      
      @Override
      public String[] value() {
        return value.toArray(new String[0]);
      }
      
      @Override
      public String[] afterEach() {
        return afterEach.toArray(new String[0]);
      }
      
      @Override
      public String[] afterAll() {
        return afterAll.toArray(new String[0]);
      }
      
      @Override
      public PlanningStrategy planExecutionWith() {
        return DEPENDENCY_BASED;
      }
    };
  }
}
