package jp.co.moneyforward.autotest.framework.testengine;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutotestEngine implements BeforeAllCallback, BeforeEachCallback, TestTemplateInvocationContextProvider, AfterEachCallback, AfterAllCallback {
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
    return actions(extensionContext, AutotestExecution.Spec::value)
        .stream()
        .map(AutotestEngine::toTestTemplateInvocationContext);
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
  
  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    AutotestRunner runner = autotestRunner(context);
    actions(context, AutotestExecution.Spec::afterAll).forEach(each -> runner.afterAll(each.value()));
  }
  
  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    AutotestRunner runner = autotestRunner(context);
    actions(context, AutotestExecution.Spec::afterEach).forEach(each -> runner.afterEach(each.value()));
  }
  
  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AutotestRunner runner = autotestRunner(context);
    actions(context, AutotestExecution.Spec::beforeEach).forEach(each -> runner.beforeEach(each.value()));
  }
  
  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    {
      AutotestRunner runner = context.getTestInstance()
                                     .filter(o -> o instanceof AutotestRunner)
                                     .map(o -> (AutotestRunner) o)
                                     .orElseThrow(RuntimeException::new);
      Map<String, Scene> sceneMap = Arrays.stream(validateTestClass(runner.getClass()).getMethods())
                                          .filter(m -> m.isAnnotationPresent(Named.class))
                                          .map(this::validateSceneProvidingMethod)
                                          .map(m -> new Entry<>(nameOf(m), invokeMethod(m, runner)))
                                          .collect(Collectors.toMap(Entry::key, Entry::value));
      AutotestExecution execution = runner.getClass().getAnnotation(AutotestExecution.class);
      AutotestExecution.Spec executionSpec = instantiateExecutionSpecLoader(execution).load(execution.defaultExecution());
      ExtensionContext.Store executionContextStore = executionContextStore(context);
      executionContextStore.put("runner", runner);
      executionContextStore.put("sceneMap", sceneMap);
      executionContextStore.put("executionSpec", executionSpec);
    }
    
    {
      AutotestRunner runner = autotestRunner(context);
      actions(context, AutotestExecution.Spec::beforeAll).forEach(each -> runner.beforeAll(each.value()));
    }
  }
  
  private static List<Entry<String, Action>> actions(ExtensionContext context, Function<AutotestExecution.Spec, String[]> toSceneNames) {
    AutotestExecution.Spec executionSpec = executionSpec(context);
    Map<String, Scene> sceneMap = sceneMap(context);
    return toActions(sceneMap,
                     ActionComposer.createActionComposer("INPUT", "OUTPUT", createExecutionEnvironment(executionSpec)),
                     toSceneNames.apply(executionSpec));
  }
  
  private static List<Entry<String, Action>> toActions(Map<String, Scene> sceneMap, ActionComposer actionComposer, String[] sceneNames) {
    return Arrays.stream(sceneNames)
                 .map(each -> new Entry<>(each, toAction(sceneMap.get(each), actionComposer)))
                 .toList();
  }
  
  private static Action toAction(Scene scene, ActionComposer actionComposer) {
    return scene.toAction(actionComposer,
                          "INPUT:" + scene.name(),
                          "OUTPUT:" + scene.name());
  }
  
  private static ExecutionEnvironment createExecutionEnvironment(AutotestExecution.Spec executionSpec) {
    return executionEnvironmentFactory(executionSpec).create();
  }
  
  @SuppressWarnings("unchecked")
  private static <E extends ExecutionEnvironment> AutotestExecution.Spec.Loader.ExecutionEnvironmentFactory<E> executionEnvironmentFactory(AutotestExecution.Spec executionSpec) {
    try {
      return executionSpec.executionEnvironmentFactory().getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  private static Map<String, Scene> sceneMap(ExtensionContext context) {
    return (Map<String, Scene>) executionContextStore(context).get("sceneMap");
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
  
  private static String nameOf(Method m) {
    Named annotation = m.getAnnotation(Named.class);
    assert annotation != null : Objects.toString(m);
    if (!Objects.equals(annotation.value(), Named.DEFAULT_VALUE))
      return annotation.value();
    return m.getName();
  }
  
  private static Scene invokeMethod(Method m, AutotestRunner runner) {
    try {
      return (Scene) m.invoke(runner);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private Method validateSceneProvidingMethod(Method m) {
    // TODO: https://app.asana.com/0/1206402209253009/1207418182714921/f
    return m;
  }
  
  private record Entry<K, V>(K key, V value) {
  }
}