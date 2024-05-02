package jp.co.moneyforward.autotest.framework.testengine;

import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSandbox {

//  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4})
  void testWithIntegers(int argument) {
    assertTrue(argument > 0);
  }
  
  @NextDakenKunTest
  @ExampleParameterResolver.ResolveIt
  public void runForestRun(int argument, int arg) {
    System.out.println("I'm not Forest.(" + argument + "," + arg + ")");
  }
  
  @NextDakenKunTest
  @ExampleParameterResolver.ResolveIt
  public void runForestRun2(int argument) {
    System.out.println("I'm not Forest2.(" + argument + ")");
  }
  
}
