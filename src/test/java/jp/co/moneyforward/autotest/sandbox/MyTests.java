package jp.co.moneyforward.autotest.sandbox;

import jp.co.moneyforward.autotest.framework.testengine.ObjectReturningTestExtension;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ObjectReturningTestExtension.class)
public class MyTests {
  
  @TestTemplate
  public String myTestMethod() {
    return "Hello, World!";
  }
  
  @TestTemplate
  public Integer anotherTestMethod() {
    return 42;
  }
}