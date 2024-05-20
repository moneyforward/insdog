package jp.co.moneyforward.autotest.framework.testengine;

import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.action.Execution;
import jp.co.moneyforward.autotest.framework.action.ExecutionCompiler;
import jp.co.moneyforward.autotest.framework.action.Play;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.junit.jupiter.api.extension.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.valid8j.fluent.Expectations.require;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class AutotestEngine implements BeforeAllCallback, TestTemplateInvocationContextProvider {
  private final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(AutotestEngine.class);
  private Play play;
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public void beforeAll(ExtensionContext context) {
    List<String> errors = new LinkedList<>();
    ExecutionEnvironment executionEnvironment = ExecutionEnvironment.load(System.getProperties());
    require(Expectations.value(errors).satisfies().empty());
    Context actionContext = Context.create();
    this.play = getPlayFrom(context);
    Execution execution = getCompilerFrom(context).compile(play, executionEnvironment);
    context.getStore(namespace).put("actionContext", actionContext);
  }
  
  private static ExecutionCompiler.Default getCompilerFrom(ExtensionContext context) {
    return new ExecutionCompiler.Default();
  }
  
  private Play getPlayFrom(ExtensionContext context) {
    return null;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
    return Stream.of(
        new TestTemplateInvocationContext() {
          @Override
          public List<Extension> getAdditionalExtensions() {
            return asList(
                new BeforeAllCallback() {
                  @Override
                  public void beforeAll(ExtensionContext context) {
                    System.out.println("BeforeAll");
                  }
                },
                new BeforeEachCallback() {
                  @Override
                  public void beforeEach(ExtensionContext context) {
                    System.out.println("BeforeEach");
                  }
                },
                new ParameterResolver() {
                  @Override
                  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
                    return true;
                  }
                  
                  @Override
                  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
                    return ActionSupport.nop();
                  }
                },
                new AfterEachCallback() {
                  @Override
                  public void afterEach(ExtensionContext context) {
                    System.out.println("AfterEach");
                  }
                },
                new AfterAllCallback() {
                  @Override
                  public void afterAll(ExtensionContext context) {
                    System.out.println("AfterAll");
                    throw new RuntimeException("Bye!");
                  }
                }
            );
          }
          
          @Override
          public String getDisplayName(int invocationIndex) {
            return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
          }
        }
    );
  }
}