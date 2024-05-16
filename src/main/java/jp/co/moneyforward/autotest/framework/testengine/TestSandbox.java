package jp.co.moneyforward.autotest.framework.testengine;

import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSandbox {

//  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4})
  void testWithIntegers(int argument) {
    assertTrue(argument > 0);
  }
  
  @AutotestCa
  public void runForestRun() {
    System.out.println("I'm not Forest.");
  }
  
  @AutotestCa
  public void runForestRun2() {
    System.out.println("I'm not Forest2");
  }
  
}
