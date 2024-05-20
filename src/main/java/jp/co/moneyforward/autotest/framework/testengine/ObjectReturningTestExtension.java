package jp.co.moneyforward.autotest.framework.testengine;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.*;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ObjectReturningTestExtension implements TestTemplateInvocationContextProvider {
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return context.getTestMethod()
                  .map(method -> method.isAnnotationPresent(TestTemplate.class))
                  .orElse(false);
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
    return Stream.of(new ObjectReturningTestInvocationContext());
  }
  
  private static class ObjectReturningTestInvocationContext implements TestTemplateInvocationContext {
    @Override
    public List<Extension> getAdditionalExtensions() {
      return Collections.singletonList(new ObjectReturningTestExecution());
    }
  }
  
  private static class ObjectReturningTestExecution implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    
    @Override
    public void beforeTestExecution(ExtensionContext context) {
      // Before test execution logic
    }
    
    @Override
    public void afterTestExecution(ExtensionContext context) {
      Method method = context.getRequiredTestMethod();
      try {
        Object testInstance = context.getRequiredTestInstance();
        Object result = method.invoke(testInstance);
        System.out.println("Result of method execution: " + result);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}