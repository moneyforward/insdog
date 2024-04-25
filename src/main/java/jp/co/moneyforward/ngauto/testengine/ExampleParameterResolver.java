package jp.co.moneyforward.ngauto.testengine;

import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public class ExampleParameterResolver implements ParameterResolver {
  @Retention(RetentionPolicy.RUNTIME)
  @ExtendWith(ExampleParameterResolver.class)
  public @interface ResolveIt {
  
  }
  static final AtomicInteger value = new AtomicInteger();
  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return true;
  }
  
  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return value.getAndIncrement();
  }
}
