package jp.co.moneyforward.autotest.framework.testengine;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.framework.action.Call;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.ClosedBy;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.utils.AutotestSupport;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.exceptions.ActionException.wrap;
import static com.github.valid8j.fluent.Expectations.require;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static jp.co.moneyforward.autotest.framework.utils.AutotestSupport.sceneCall;

public class AutotestEngine implements BeforeAllCallback, BeforeEachCallback, TestTemplateInvocationContextProvider, AfterEachCallback, AfterAllCallback {
  private static final Logger LOGGER = LoggerFactory.getLogger(AutotestEngine.class);
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
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
      validateTestClass(runner.getClass());
      Map<String, Call.SceneCall> sceneCallMap = Arrays.stream(validateTestClass(runner.getClass()).getMethods())
                                                       .filter(m -> m.isAnnotationPresent(Named.class))
                                                       .filter(m -> !m.isAnnotationPresent(Disabled.class))
                                                       .map(this::validateSceneProvidingMethod)
                                                       .map(m -> new Entry<>(nameOf(m), invokeMethod(m, runner)))
                                                       .collect(toMap(Entry::key, Entry::value));
      
      ExecutionPlan executionPlan = planExecution(loadExecutionSpec(runner),
                                                  sceneCallGraph(runner.getClass()),
                                                  closers(runner.getClass()));
      ExtensionContext.Store executionContextStore = executionContextStore(context);
      
      executionContextStore.put("runner", runner);
      executionContextStore.put("sceneCallMap", sceneCallMap);
      executionContextStore.put("executionPlan", executionPlan);
    }
    
    {
      AutotestRunner runner = autotestRunner(context);
      ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
      configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-beforeAll.log"), Level.INFO);
      actions(executionPlan(context),
              ExecutionPlan::beforeAll,
              sceneCallMap(context),
              executionEnvironment)
          .forEach(each -> runner.beforeAll(each.value()));
    }
  }
  
  @Override
  public void beforeEach(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-before.log"), Level.INFO);
    AutotestRunner runner = autotestRunner(context);
    actions(executionPlan(context),
            ExecutionPlan::beforeEach,
            sceneCallMap(context),
            executionEnvironment)
        .forEach(each -> runner.beforeEach(each.value()));
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-main.log"), Level.INFO);
  }
  
  @Override
  public void afterEach(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-after.log"), Level.INFO);
    AutotestRunner runner = autotestRunner(context);
    List<ExceptionEntry> errors = new ArrayList<>();
    actions(executionPlan(context),
            ExecutionPlan::afterEach,
            sceneCallMap(context),
            executionEnvironment)
        .forEach(each -> runActionEntryRollingForwardOnErrors(each,
                                                              errors,
                                                              () -> runner.afterEach(each.value())));
    if (!errors.isEmpty()) reportErrors(errors);
  }
  
  @Override
  public void afterAll(ExtensionContext context) {
    AutotestRunner runner = autotestRunner(context);
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-afterAll.log"), Level.INFO);
    List<ExceptionEntry> errors = new ArrayList<>();
    actions(executionPlan(context),
            ExecutionPlan::afterAll,
            sceneCallMap(context),
            executionEnvironment)
        .forEach(each -> runActionEntryRollingForwardOnErrors(each,
                                                              errors,
                                                              () -> runner.afterAll(each.value())));
    if (!errors.isEmpty()) reportErrors(errors);
  }
  
  private static AutotestExecution.Spec loadExecutionSpec(AutotestRunner runner) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    AutotestExecution execution = runner.getClass()
                                        .getAnnotation(AutotestExecution.class);
    return instantiateExecutionSpecLoader(execution).load(execution.defaultExecution());
  }
  
  private static ExecutionPlan planExecution(AutotestExecution.Spec executionSpec, Map<String, List<String>> sceneCallGraph, Map<String, String> closers) {
    return executionSpec.planExecutionWith().planExecution(executionSpec, sceneCallGraph, closers);
  }
  
  private static Map<String, List<String>> sceneCallGraph(Class<?> accessModelClass) {
    Map<String, List<String>> sceneCallGraph = new LinkedHashMap<>();
    Arrays.stream(accessModelClass.getMethods())
          .filter(m -> m.isAnnotationPresent(Named.class))
          .filter(m -> !m.isAnnotationPresent(Disabled.class))
          .forEach(m -> {
            if (m.isAnnotationPresent(DependsOn.class)) {
              sceneCallGraph.put(nameOf(m), Arrays.stream(m.getAnnotation(DependsOn.class).value())
                                                  .map(DependsOn.Parameter::sourceSceneName)
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

  private static void runActionEntryRollingForwardOnErrors(Entry<String, Action> each, List<ExceptionEntry> errors, Runnable runnable) {
    try {
      LOGGER.info("Executing: {}", each.key());
      runnable.run();
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (Throwable e) {
      LOGGER.warn("Error executing action: {}", each.key(), e);
      errors.add(new ExceptionEntry(each.key(), e));
    }
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
                                                     Map<String, Call.SceneCall> sceneCallMap,
                                                     ExecutionEnvironment executionEnvironment) {
    return toActions(sceneCallMap,
                     createActionComposer(executionEnvironment),
                     toSceneNames.apply(executionPlan));
  }
  
  private static List<Entry<String, Action>> toActions(Map<String, Call.SceneCall> sceneCallMap, ActionComposer actionComposer, List<String> sceneNames) {
    return sceneNames.stream()
                     .filter(sceneCallMap::containsKey)
                     .map((String each) -> new Entry<>(each, toAction(sceneCallMap.get(each), actionComposer)))
                     .toList();
  }
  
  private static Action toAction(Call.SceneCall sceneCall, ActionComposer actionComposer) {
    return actionComposer.create(sceneCall);
  }
  
  public static ExecutionEnvironment createExecutionEnvironment(String testClassName) {
    require(Expectations.value(testClassName).toBe().notNull());
    return new ExecutionEnvironment() {
      @Override
      public String testClassName() {
        return testClassName;
      }
      
      @Override
      public Optional<String> testSceneName() {
        return Optional.empty();
      }
    };
  }
  
  private static ExecutionEnvironment createExecutionEnvironment(ExtensionContext extensionContext) {
    return createExecutionEnvironment(
        extensionContext.getTestClass()
                        .map(Class::getCanonicalName)
                        .orElse("Unknown-" + System.currentTimeMillis()));
  }
  
  @SuppressWarnings("unchecked")
  private static Map<String, Call.SceneCall> sceneCallMap(ExtensionContext context) {
    return (Map<String, Call.SceneCall>) executionContextStore(context).get("sceneCallMap");
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
  
  public static String nameOf(Method m) {
    Named annotation = m.getAnnotation(Named.class);
    assert annotation != null : Objects.toString(m);
    if (!Objects.equals(annotation.value(), Named.DEFAULT_VALUE))
      return annotation.value();
    return m.getName();
  }
  
  private static Call.SceneCall invokeMethod(Method m, AutotestRunner runner) {
    try {
      Scene scene = (Scene) m.invoke(runner);
      return sceneCall(nameOf(m), scene, dependenciesOf(m));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static List<Resolver> dependenciesOf(Method m) {
    if (!m.isAnnotationPresent(DependsOn.class))
      return emptyList();
    return Arrays.stream(m.getAnnotation(DependsOn.class).value())
                 .map(v -> new Resolver(v.name(), AutotestSupport.valueFrom(v.sourceSceneName(), v.fieldNameInSourceScene())))
                 .toList();
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
          .forEach((s, appender1) -> config.getRootLogger().removeAppender(s));
    
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
            return parameterContext.getIndex() == 0 && parameterContext.getParameter().getType().isAssignableFrom(Action.class);
          }
          
          @Override
          public Action resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
            return actionEntry.value();
          }
        });
      }
      
      @Override
      public String getDisplayName(int invocationIndex) {
        return TestTemplateInvocationContext.super.getDisplayName(invocationIndex) + ":" + actionEntry.key();
      }
    };
  }
  
  private record Entry<K, V>(K key, V value) {
  }
  
  public record ExecutionPlan(List<String> beforeAll,
                              List<String> beforeEach,
                              List<String> value,
                              List<String> afterEach,
                              List<String> afterAll) {
  }
  
  record ExceptionEntry(String name, Throwable exception) {
  }
}