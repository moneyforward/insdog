package jp.co.moneyforward.autotest.framework.testengine;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.framework.action.Call;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.core.Resolver;
import jp.co.moneyforward.autotest.framework.utils.AutotestSupport;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.valid8j.fluent.Expectations.require;
import static com.github.valid8j.fluent.Expectations.value;
import static java.util.Collections.emptyList;
import static jp.co.moneyforward.autotest.framework.utils.AutotestSupport.sceneCall;

public class AutotestEngine implements BeforeAllCallback, BeforeEachCallback, TestTemplateInvocationContextProvider, AfterEachCallback, AfterAllCallback {
  private static final Logger LOGGER = LogManager.getLogger(AutotestEngine.class);
  
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
    return actions(executionSpec(context),
                   AutotestExecution.Spec::value,
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
      Map<String, Call.SceneCall> sceneCallMap = Arrays.stream(validateTestClass(runner.getClass()).getMethods())
                                                       .filter(m -> m.isAnnotationPresent(Named.class))
                                                       .filter(m -> !m.isAnnotationPresent(Disabled.class))
                                                       .map(this::validateSceneProvidingMethod)
                                                       .map(m -> new Entry<>(nameOf(m), invokeMethod(m, runner)))
                                                       .collect(Collectors.toMap(Entry::key, Entry::value));
      AutotestExecution execution = runner.getClass().getAnnotation(AutotestExecution.class);
      AutotestExecution.Spec executionSpec = instantiateExecutionSpecLoader(execution).load(execution.defaultExecution());
      ExtensionContext.Store executionContextStore = executionContextStore(context);
      executionContextStore.put("runner", runner);
      executionContextStore.put("sceneCallMap", sceneCallMap);
      executionContextStore.put("executionSpec", executionSpec);
      
    }
    
    {
      AutotestRunner runner = autotestRunner(context);
      ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
      actions(executionSpec(context),
              AutotestExecution.Spec::beforeAll,
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
    actions(executionSpec(context),
            AutotestExecution.Spec::beforeEach,
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
    actions(executionSpec(context),
            AutotestExecution.Spec::afterEach,
            sceneCallMap(context),
            executionEnvironment)
        .forEach(each -> runner.afterEach(each.value()));
  }
  
  @Override
  public void afterAll(ExtensionContext context) {
    AutotestRunner runner = autotestRunner(context);
    ExecutionEnvironment executionEnvironment = createExecutionEnvironment(context).withSceneName(context.getDisplayName());
    configureLogging(executionEnvironment.testOutputFilenameFor("autotestExecution-afterAll.log"), Level.INFO);
    actions(executionSpec(context),
            AutotestExecution.Spec::afterAll,
            sceneCallMap(context),
            executionEnvironment)
        .forEach(each -> runner.afterAll(each.value()));
  }
  
  
  private static List<Entry<String, Action>> actions(AutotestExecution.Spec executionSpec,
                                                     Function<AutotestExecution.Spec, String[]> toSceneNames,
                                                     Map<String, Call.SceneCall> sceneCallMap,
                                                     ExecutionEnvironment executionEnvironment) {
    return toActions(sceneCallMap,
                     createActionComposer(executionEnvironment),
                     toSceneNames.apply(executionSpec));
  }
  
  private static List<Entry<String, Action>> toActions(Map<String, Call.SceneCall> sceneCallMap, ActionComposer actionComposer, String[] sceneNames) {
    return Arrays.stream(sceneNames)
                 .filter(sceneCallMap::containsKey)
                 .map((String each) -> new Entry<>(each, toAction(sceneCallMap.get(each), actionComposer)))
                 .toList();
  }
  
  private static Action toAction(Call.SceneCall sceneCall, ActionComposer actionComposer) {
    return actionComposer.create(sceneCall);
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
  
  private static AutotestExecution.Spec executionSpec(ExtensionContext context) {
    return (AutotestExecution.Spec) executionContextStore(context).get("executionSpec");
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
    
    logFilePath.getParent().toFile().mkdirs();
    
    
    PatternLayout layout = PatternLayout.newBuilder()
                                        .withPattern("%d{ISO8601} [%t] %-5p %c %x - %m%n")
                                        .build();
    
    FileAppender appender = FileAppender.newBuilder()
                                        .withFileName(logFilePath.toString())
                                        .withAppend(true)
                                        .withLocking(false)
                                        .setName("FileAppender")
                                        .setImmediateFlush(true)
                                        .setLayout(layout)
                                        .setConfiguration(config)
                                        .build();
    
    appender.start();
    
    // Remove all existing appenders
    config.getRootLogger()
          .getAppenders()
          .forEach((s, appender1) -> config.getRootLogger().removeAppender(s));
    
    // Add the new appender
    config.getRootLogger().addAppender(appender, logLevel, null);
    
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
}