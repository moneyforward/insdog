package jp.co.moneyforward.ngauto.testengine;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.stream.Stream;

public class MyCustomTestEngine implements TestTemplateInvocationContextProvider {
  
  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }
  
  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
    return Stream.of(
        new TestTemplateInvocationContext() {
          @Override
          public String getDisplayName(int invocationIndex) {
            return TestTemplateInvocationContext.super.getDisplayName(invocationIndex);
          }
        }
    );
  }
  
}