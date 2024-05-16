package jp.co.moneyforward.autotest.framework.testengine;

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

public class AutotestEngine implements BeforeAllCallback, TestTemplateInvocationContextProvider {
  private final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(AutotestEngine.class);
  
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
    Play play = null;
    Execution execution = new ExecutionCompiler.Default().compile(play, executionEnvironment);
    context.getStore(namespace).put("actionContext", actionContext);
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
        },
        new TestTemplateInvocationContext() {
          @Override
          public String getDisplayName(int invocationIndex) {
            return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
          }
        }
    );
  }
}