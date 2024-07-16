package jp.co.moneyforward.autotest.framework.testengine;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.valid8j.pcond.fluent.Statement;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.action.SceneCall;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.utils.Valid8JCliches.MakePrintable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.exceptions.ActionException.wrap;
import static com.github.valid8j.classic.Requires.requireNonNull;
import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.internals.InternalUtils.wrapIfNecessary;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static jp.co.moneyforward.autotest.framework.action.AutotestSupport.sceneCall;

/**
 * The test execution engine of the **autotest-ca**.
 *
 * In the implementation of this engine, the steps performed during a test class execution are following:
 *
 * 1. **beforeAll:** Every scene in this step is executed in the order they are shown in the execution plan.
 * 2. **beforeEach:** For each scene in the **value (main)** step, every scene in this step is executed in the order.
 * When a failure occurs, the rest will not be executed.
 * 3. **value (or main):** This step is the main part of the entire test.
 * This stage was named **value** to make the user test scenario class as simple as possible.
 * (In Java, in order to omit typing an annotation's method name, we need to name it `value`)
 * In the future, we may change it to `main`.
 * 4. **afterEach:** Scenes in this step are executed in the provided order, after each **value (or main)** scene is performed even if on a failure.
 * In this step, even if a failure happens in an **afterEach** scene, the subsequent scenes should still be executed.
 * 5. **afterAll:** Scenes in this step are executed in the provided order, after all the scenes in the **afterEach** for the last of the **value (or main)** is executed.
 * In this step, even if a failure happens in an **afterAll** scene, the subsequent scenes should still be executed.
 *
 * Note that the "execution plan" and which scenes a user specifies to execute are not the same.
 * The former is modeled by `ExecutionPlan` and the latter is modeled by the `AutotestExecution.Spec`.
 * The `PlanningStrategy` instance interprets the `AutotestExecution.Spec` and creates an `ExecutionPlan`.
 * The discussion above is about the `ExecutionPlan`.
 *
 * Also, a `PlanningStrategy` should be designed in a way where scenes that a user specifies explicitly are included in its resulting execution plan.
 *
 * With this separation, **autotest-ca** allows users to specify scenes that really want to execute directly.
 *
 * @see AutotestExecution.Spec
 * @see PlanningStrategy
 */
public class AutotestEngine implements BeforeAllCallback, BeforeEachCallback, TestTemplateInvocationContextProvider, AfterEachCallback, AfterAllCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(AutotestEngine.class);
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName(), "main");
    return actions(executionPlan(context),
                   ExecutionPlan::value,
                   sceneCallMap(context),
                   executionEnvironment).stream()
                                        .map(AutotestEngine::toTestTemplateInvocationContext);
  }
  
  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    {
      AutotestRunner runner = context.getTestInstance()
                                     .filter(o -> o instanceof AutotestRunner)
                                     .map(o -> (AutotestRunner) o)
                                     .orElseThrow(RuntimeException::new);
      Class<?> accessModelClass = validateTestClass(runner.getClass());
      Map<String, SceneCall> sceneCallMap = Arrays.stream(accessModelClass.getMethods())
                                                  .filter(m -> m.isAnnotationPresent(Named.class))
                                                  .filter(m -> !m.isAnnotationPresent(Disabled.class))
                                                  .map(this::validateSceneProvidingMethod)
                                                  .map(m -> new Entry<>(nameOf(m), methodToSceneCall(accessModelClass, m, runner)))
                                                  .collect(toMap(Entry::key, Entry::value));
      
      AutotestExecution.Spec spec = loadExecutionSpec(runner);
      ExecutionPlan executionPlan = planExecution(spec,
                                                  sceneCallGraph(runner.getClass()),
                                                  closers(runner.getClass()),
                                                  assertions(runner.getClass()));
      assert Contracts.explicitlySpecifiedScenesAreAllCoveredInCorrespondingPlannedStage(spec, executionPlan);
      ExtensionContext.Store executionContextStore = executionContextStore(context);
      
      executionContextStore.put("runner", runner);
      executionContextStore.put("sceneCallMap", sceneCallMap);
      executionContextStore.put("executionPlan", executionPlan);
    }
    
    {
      AutotestRunner runner = autotestRunner(context);
      ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName(), "beforeAll");
      configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-beforeAll.log"), Level.INFO);
      String stageName = "beforeAll";
      List<String> out = new ArrayList<>();
      actions(executionPlan(context),
              ExecutionPlan::beforeAll,
              sceneCallMap(context),
              executionEnvironment)
          .stream()
          .map(each -> runActionEntryWith(each, a -> runner.beforeAll(a.value(), runner.createWriter(out))))
          .peek(SceneExecutionResult::throwIfFailed)
          // In order to ensure all the actions are finished, accumulate the all entries into the list, first.
          // Then, stream again. Otherwise, the log will not become so readable.
          .toList()
          .forEach(r -> {
            LOGGER.info(r.composeMessageHeader(stageName));
            out.forEach(l -> LOGGER.info(String.format("%-11s %s", stageName + ":", l)));
          });
    }
  }
  
  @Override
  public void beforeEach(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName(), "beforeEach");
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-before.log"), Level.INFO);
    AutotestRunner runner = autotestRunner(context);
    String stageName = "beforeEach";
    List<String> out = new ArrayList<>();
    actions(executionPlan(context),
            ExecutionPlan::beforeEach,
            sceneCallMap(context),
            executionEnvironment)
        .stream()
        .map(each -> runActionEntryWith(each, a -> runner.beforeEach(a.value(), runner.createWriter(out))))
        .peek(SceneExecutionResult::throwIfFailed)
        // In order to ensure all the actions are finished, accumulate the all entries into the list, first.
        // Then, stream again. Otherwise, the log will not become so readable.
        .toList()
        .forEach(r -> {
          LOGGER.info(r.composeMessageHeader(stageName));
          out.forEach(l -> LOGGER.info(String.format("%-11s %s", stageName + ":", l)));
        });
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-main.log"), Level.INFO);
  }
  
  @Override
  public void afterEach(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName(), "afterEach");
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-after.log"), Level.INFO);
    AutotestRunner runner = autotestRunner(context);
    List<ExceptionEntry> errors = new ArrayList<>();
    String stageName = "afterEach";
    List<String> out = new ArrayList<>();
    actions(executionPlan(context),
            ExecutionPlan::afterEach,
            sceneCallMap(context),
            executionEnvironment)
        .stream()
        .map(each -> runActionEntryWith(each, a -> runner.afterEach(a.value(), runner.createWriter(out))))
        .peek(r -> r.exception()
                    .ifPresent(t -> errors.add(new ExceptionEntry(r.name(), t))))
        // In order to ensure all the actions are finished, accumulate the all entries into the list, first.
        // Then, stream again. Otherwise, the log will not become so readable.
        .toList()
        .forEach(r -> {
          LOGGER.info(r.composeMessageHeader(stageName));
          out.forEach(l -> LOGGER.info(String.format("%-11s %s", stageName + ":", l)));
        });
    if (!errors.isEmpty()) reportErrors(errors);
  }
  
  @Override
  public void afterAll(ExtensionContext context) {
    AutotestRunner runner = autotestRunner(context);
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName(), "afterAll");
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-afterAll.log"), Level.INFO);
    List<ExceptionEntry> errors = new ArrayList<>();
    String stageName = "afterAll";
    List<String> out = new ArrayList<>();
    actions(executionPlan(context),
            ExecutionPlan::afterAll,
            sceneCallMap(context),
            executionEnvironment)
        .stream()
        .map(each -> runActionEntryWith(each, a -> runner.afterAll(a.value(), runner.createWriter(out))))
        .peek(r -> r.exception()
                    .ifPresent(t -> errors.add(new ExceptionEntry(r.name(), t))))
        // In order to ensure all the actions are finished, accumulate the all entries into the list, first.
        // Then, stream again. Otherwise, the log will not become so readable.
        .toList()
        .forEach(r -> {
          LOGGER.info(r.composeMessageHeader(stageName));
          out.forEach(l -> LOGGER.info(String.format("%-11s %s", stageName + ":", l)));
        });
    if (!errors.isEmpty()) reportErrors(errors);
  }
  
  public static ExecutionEnvironment createExecutionEnvironment(String testClassName) {
    require(value(testClassName).toBe().notNull());
    return new ExecutionEnvironment() {
      @Override
      public String testClassName() {
        return testClassName;
      }
      
      @Override
      public Optional<String> testSceneName() {
        return Optional.empty();
      }
      
      @Override
      public String stepName() {
        return "unknown";
      }
    };
  }
  
  
  private static AutotestExecution.Spec loadExecutionSpec(AutotestRunner runner) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    AutotestExecution execution = runner.getClass()
                                        .getAnnotation(AutotestExecution.class);
    return instantiateExecutionSpecLoader(execution).load(execution.defaultExecution());
  }
  
  private static ExecutionPlan planExecution(AutotestExecution.Spec executionSpec,
                                             Map<String, List<String>> sceneCallGraph,
                                             Map<String, String> closers,
                                             Map<String, List<String>> assertions) {
    return executionSpec.planExecutionWith()
                        .planExecution(executionSpec, sceneCallGraph, closers, assertions);
  }
  
  private static Map<String, List<String>> sceneCallGraph(Class<?> accessModelClass) {
    Map<String, List<String>> sceneCallGraph = new LinkedHashMap<>();
    Arrays.stream(accessModelClass.getMethods())
          .filter(m -> m.isAnnotationPresent(Named.class))
          .filter(m -> !m.isAnnotationPresent(Disabled.class))
          .forEach(m -> {
            if (m.isAnnotationPresent(DependsOn.class)) {
              sceneCallGraph.put(nameOf(m), Arrays.stream(m.getAnnotation(DependsOn.class).value())
                                                  .toList());
            } else {
              sceneCallGraph.put(nameOf(m), emptyList());
            }
          });
    return sceneCallGraph;
  }
  
  private static Map<String, String> closers(Class<?> accessModelClass) {
    Map<String, String> closers = new LinkedHashMap<>();
    Arrays.stream(accessModelClass.getMethods())
          .filter(m -> m.isAnnotationPresent(Named.class))
          .filter(m -> !m.isAnnotationPresent(Disabled.class))
          .forEach(m -> {
            if (m.isAnnotationPresent(ClosedBy.class)) {
              closers.put(nameOf(m), m.getAnnotation(ClosedBy.class).value());
            }
          });
    return closers;
  }
  
  private static Map<String, List<String>> assertions(Class<? extends AutotestRunner> accessModelClass) {
    Map<String, List<String>> ret = new LinkedHashMap<>();
    Arrays.stream(accessModelClass.getMethods())
          .filter(m -> m.isAnnotationPresent(Named.class))
          .filter(m -> !m.isAnnotationPresent(Disabled.class))
          .forEach(m -> {
            if (m.isAnnotationPresent(When.class)) {
              for (String each : m.getAnnotation(When.class).value()) {
                ret.putIfAbsent(each, new LinkedList<>());
                ret.get(each).add(nameOf(m));
              }
            }
          });
    return ret;
  }
  
  private static void reportErrors(List<ExceptionEntry> errors) {
    errors.forEach(each -> {
      LOGGER.error("{} {}", each.name, each.exception.getMessage());
      LOGGER.debug("{}", each.exception.getMessage(), each.exception);
    });
    throw wrap(errors.getFirst().exception);
  }
  
  
  private static List<Entry<String, Action>> actions(ExecutionPlan executionPlan,
                                                     Function<ExecutionPlan, List<String>> toSceneNames,
                                                     Map<String, SceneCall> sceneCallMap,
                                                     ExecutionEnvironment executionEnvironment) {
    return toActions(sceneCallMap,
                     createActionComposer(executionEnvironment),
                     toSceneNames.apply(executionPlan));
  }
  
  private static List<Entry<String, Action>> toActions(Map<String, SceneCall> sceneCallMap, ActionComposer actionComposer, List<String> sceneNames) {
    return sceneNames.stream()
                     .filter(sceneCallMap::containsKey)
                     .map((String each) -> new Entry<>(each,
                                                       toAction(sceneCallMap.get(each),
                                                                actionComposer)))
                     .toList();
  }
  
  private static Action toAction(SceneCall currentSceneCall, ActionComposer actionComposer) {
    return actionComposer.create(currentSceneCall, currentSceneCall.assignmentResolvers().orElseThrow());
  }
  
  private static ExecutionEnvironment createExecutionEnvironment(ExtensionContext extensionContext) {
    return createExecutionEnvironment(
        extensionContext.getTestClass()
                        .map(Class::getCanonicalName)
                        .orElse("Unknown-" + System.currentTimeMillis()));
  }
  
  @SuppressWarnings("unchecked")
  private static Map<String, SceneCall> sceneCallMap(ExtensionContext context) {
    return (Map<String, SceneCall>) executionContextStore(context).get("sceneCallMap");
  }
  
  private static ExecutionPlan executionPlan(ExtensionContext context) {
    return (ExecutionPlan) executionContextStore(context).get("executionPlan");
  }
  
  private static AutotestRunner autotestRunner(ExtensionContext context) {
    return (AutotestRunner) executionContextStore(context).get("runner");
  }
  
  private static ExtensionContext.Store executionContextStore(ExtensionContext context) {
    return context.getStore(ExtensionContext.Namespace.GLOBAL);
  }
  
  private static AutotestExecution.Spec.Loader instantiateExecutionSpecLoader(AutotestExecution execution) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return execution.executionSpecLoaderClass().getConstructor().newInstance();
  }
  
  private static <E> Class<E> validateTestClass(Class<E> aClass) {
    return aClass;
  }
  
  private static String nameOf(Method m) {
    Named annotation = m.getAnnotation(Named.class);
    assert annotation != null : Objects.toString(m);
    if (!Objects.equals(annotation.value(), Named.DEFAULT_VALUE))
      return annotation.value();
    return m.getName();
  }
  
  private static Optional<Method> findMethodByName(String name, Class<?> klass) {
    return Arrays.stream(klass.getMethods())
                 .filter(m -> m.isAnnotationPresent(Named.class))
                 .filter(m -> Objects.equals(nameOf(m), name))
                 .findFirst();
  }
  
  private static SceneCall methodToSceneCall(Class<?> accessModelClass, Method method, AutotestRunner runner) {
    return sceneCall(nameOf(method),
                     createSceneFromMethod(method, runner),
                     variableResolversFor(accessModelClass, method));
  }
  
  private static Scene createSceneFromMethod(Method method, AutotestRunner runner) {
    try {
      return (Scene) method.invoke(runner);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static List<Resolver> variableResolversFor(Class<?> accessModelClass, Method method) {
    return Stream.concat(variableResolversFor(method,
                                              accessModelClass,
                                              DependsOn.class,
                                              m -> m.getAnnotation(DependsOn.class).value()).stream(),
                         variableResolversFor(method,
                                              accessModelClass,
                                              When.class,
                                              m -> m.getAnnotation(When.class).value()).stream())
                 .toList();
  }
  
  private static List<Resolver> variableResolversFor(Method m,
                                                     Class<?> accessModelClass,
                                                     Class<? extends Annotation> dependencyAnnotationClass,
                                                     Function<Method, String[]> dependenciesResolver) {
    if (!m.isAnnotationPresent(dependencyAnnotationClass))
      return emptyList();
    return variableResolversFor(dependenciesResolver.apply(m),
                                dependencySceneName -> exportedVariablesOf(accessModelClass, dependencySceneName));
  }
  
  private static List<Resolver> variableResolversFor(String[] dependencySceneNames,
                                                     Function<String, List<String>> exportedVariables) {
    return Arrays.stream(dependencySceneNames)
                 .flatMap((String dependencySceneName) -> exportedVariables.apply(dependencySceneName).stream()
                                                                           .map(e -> Resolver.resolverFor(dependencySceneName,
                                                                                                          e)))
                 .toList();
  }
  
  private static List<String> exportedVariablesOf(Class<?> accessModelClass, String methodName) {
    return List.of(findMethodByName(methodName, accessModelClass)
                       .orElseThrow()
                       .getAnnotation(Export.class)
                       .value());
  }
  
  private Method validateSceneProvidingMethod(Method m) {
    // TODO: https://app.asana.com/0/1206402209253009/1207418182714921/f
    return m;
  }
  
  public static ActionComposer createActionComposer(ExecutionEnvironment executionEnvironment) {
    return ActionComposer.createActionComposer(executionEnvironment);
  }
  
  public static void configureLogging(Path logFilePath, Level logLevel) {
    LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    Configuration config = ctx.getConfiguration();
    
    File logDirectory = logFilePath.getParent().toFile();
    if (logDirectory.mkdirs())
      System.err.println("Directory: <" + logDirectory.getAbsolutePath() + "> was created for logging.");
    
    PatternLayout layout = PatternLayout.newBuilder()
                                        .withPattern("[%-5p] [%d{yyyy/MM/dd HH:mm:ss.SSS}] [%t] - %m%n")
                                        .build();
    
    FileAppender fileAppender = FileAppender.newBuilder()
                                            .withFileName(logFilePath.toString())
                                            .withAppend(true)
                                            .withLocking(false)
                                            .setName("FileAppender")
                                            .setImmediateFlush(true)
                                            .setLayout(layout)
                                            .setConfiguration(config)
                                            .build();
    fileAppender.start();
    // Create a Console Appender
    ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                                                     .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                                                     .setName("ConsoleLogger")
                                                     .setLayout(layout)
                                                     .build();
    consoleAppender.start();
    
    
    // Remove all existing appenders
    config.getRootLogger()
          .getAppenders()
          .forEach((s, appender) -> config.getRootLogger().removeAppender(s));
    
    config.addAppender(fileAppender);
    config.addAppender(consoleAppender);
    
    // Add the new fileAppender
    config.getRootLogger().addAppender(fileAppender, logLevel, null);
    config.getRootLogger().addAppender(consoleAppender, logLevel, null);
    
    // Set the log level
    LoggerConfig loggerConfig = config.getRootLogger();
    loggerConfig.setLevel(logLevel);
    
    // Apply changes
    ctx.updateLoggers();
  }
  
  private static TestTemplateInvocationContext toTestTemplateInvocationContext(Entry<String, Action> actionEntry) {
    return new TestTemplateInvocationContext() {
      @Override
      public List<Extension> getAdditionalExtensions() {
        return List.of(new ParameterResolver() {
          @Override
          public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return parameterContext.getParameter().getType().isAssignableFrom(Action.class) ||
                parameterContext.getParameter().getType().isAssignableFrom(String.class) ||
                parameterContext.getParameter().getType().isAssignableFrom(Writer.class);
          }
          
          @Override
          public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            if (parameterContext.getParameter().getType().isAssignableFrom(Action.class)) {
              return actionEntry.value();
            } else if (parameterContext.getParameter().getType().isAssignableFrom(String.class)) {
              return actionEntry.key();
            } else if (parameterContext.getParameter().getType().isAssignableFrom(Writer.class)) {
              return autotestRunner(extensionContext).createWriter(new ArrayList<>());
            }
            throw new AssertionError();
          }
        });
      }
      
      @Override
      public String getDisplayName(int invocationIndex) {
        return TestTemplateInvocationContext.super.getDisplayName(invocationIndex) + ":" + actionEntry.key();
      }
    };
  }
  
  private static SceneExecutionResult runActionEntryWith(Entry<String, Action> actionEntry, Consumer<Entry<String, Action>> runner) {
    try {
      runner.accept(actionEntry);
      return new SceneExecutionResult(actionEntry.key(), null);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (Throwable e) {
      return new SceneExecutionResult(actionEntry.key(), e);
    }
  }
  
  private record Entry<K, V>(K key, V value) {
  }
  
  /**
   * This record models an execution plan created from the requirement given by user.
   *
   * The framework executes the scenes returned by each method.
   * It is a design concern of the test engine (`AutotestEngine`) how scenes within the stage.
   * For instance, whether they should be executed sequentially or concurrently, although sequential execution will be preferred in most cases.
   * The engine should execute each state as an instance of this record gives.
   * All scenes in `beforeAll` should be executed in the `beforeAll` stage, nothing else at all, in the order, where they are returned,
   * as long as they give no errors, and as such.
   *
   * In situations, where a non-directly required scenes need to be executed for some reason (E.g., a scene in a stage requires some others to be executed beforehand),
   * including the scenes implicitly and sorting out the execution order appropriately is the responsibility of the `PlanningStrategy`, not the engine.
   *
   * @param beforeAll  The names of the scenes to be executed in the `beforeAll` scene.
   * @param beforeEach The names of the scenes to be executed in the `beforeEach` scene.
   * @param value      The names of the scenes to be executed as real tests.
   * @param afterEach  The names of the scenes to be executed in the `afterEach` scene.
   * @param afterAll   The names of the scenes to be executed in the `afterAll` scene.
   * @see AutotestEngine
   * @see PlanningStrategy
   */
  public record ExecutionPlan(List<String> beforeAll,
                              List<String> beforeEach,
                              List<String> value,
                              List<String> afterEach,
                              List<String> afterAll) {
  }
  
  /**
   * A class that models a result of a scene execution.
   */
  static class SceneExecutionResult {
    private final String name;
    private final Throwable exception;
    
    SceneExecutionResult(String name, Throwable exception) {
      this.name = requireNonNull(name);
      this.exception = exception;
    }
    
    
    public String name() {
      return this.name;
    }
    
    public Optional<Throwable> exception() {
      return Optional.ofNullable(this.exception);
    }
    
    public boolean hasSucceeded() {
      return this.exception == null;
    }
    
    public void throwIfFailed() {
      if (hasSucceeded())
        return;
      throw wrapIfNecessary(this.exception);
    }
    
    private String composeMessageHeader(String stageName) {
      return composeMessageHeader(this, stageName);
    }
    
    private static String composeMessageHeader(SceneExecutionResult r, String stageName) {
      return String.format("%-11s [%1s]%-20s %-40s",
                           stageName + ":",
                           r.hasSucceeded() ? "o"
                                            : "E",
                           r.name(),
                           r.exception()
                            .map(Throwable::getMessage)
                            .orElse(""));
    }
  }
  
  record ExceptionEntry(String name, Throwable exception) {
  }
  
  enum Contracts {
    ;
    
    private static boolean explicitlySpecifiedScenesAreAllCoveredInCorrespondingPlannedStage(AutotestExecution.Spec spec, ExecutionPlan executionPlan) {
      return all(plannedScenesCoverAllSpecifiedScenes(spec,
                                                      specifiedScenesInStage("beforeAll", (AutotestExecution.Spec v) -> Arrays.asList(v.beforeAll())),
                                                      predicatePlannedScenesContainsSpecifiedScene("beforeAll", executionPlan.beforeAll())),
                 plannedScenesCoverAllSpecifiedScenes(spec,
                                                      specifiedScenesInStage("beforeEach", (AutotestExecution.Spec v) -> Arrays.asList(v.beforeEach())),
                                                      predicatePlannedScenesContainsSpecifiedScene("beforeEach", executionPlan.beforeEach())),
                 plannedScenesCoverAllSpecifiedScenes(spec,
                                                      specifiedScenesInStage("value", (AutotestExecution.Spec v) -> Arrays.asList(v.value())),
                                                      predicatePlannedScenesContainsSpecifiedScene("value", executionPlan.value())),
                 plannedScenesCoverAllSpecifiedScenes(spec,
                                                      specifiedScenesInStage("afterEach", (AutotestExecution.Spec v) -> Arrays.asList(v.afterEach())),
                                                      predicatePlannedScenesContainsSpecifiedScene("afterEach", executionPlan.afterEach())),
                 plannedScenesCoverAllSpecifiedScenes(spec,
                                                      specifiedScenesInStage("afterAll", (AutotestExecution.Spec v) -> Arrays.asList(v.afterAll())),
                                                      predicatePlannedScenesContainsSpecifiedScene("afterAll", executionPlan.afterAll()))
      );
    }
    
    private static Predicate<String> predicatePlannedScenesContainsSpecifiedScene(String stageName, List<String> plannedScenes) {
      return MakePrintable.<String>predicate(plannedScenes::contains).$("executionPlan." + stageName + ".contains");
    }
    
    private static Function<AutotestExecution.Spec, List<String>> specifiedScenesInStage(String stageName, Function<AutotestExecution.Spec, List<String>> scenesInStage) {
      return MakePrintable.function(scenesInStage)
                          .$("spec." + stageName);
    }
    
    private static Statement<AutotestExecution.Spec> plannedScenesCoverAllSpecifiedScenes(AutotestExecution.Spec spec,
                                                                                          Function<AutotestExecution.Spec, List<String>> specifiedScenesInSpec,
                                                                                          Predicate<String> plannedScenesContains) {
      return value(spec).function(specifiedScenesInSpec)
                        .asListOf(String.class)
                        .stream()
                        .toBe()
                        .allMatch(plannedScenesContains);
    }
  }
}