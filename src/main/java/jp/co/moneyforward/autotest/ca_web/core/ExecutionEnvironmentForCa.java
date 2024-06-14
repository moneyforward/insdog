package jp.co.moneyforward.autotest.ca_web.core;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.Optional;

public class ExecutionEnvironmentForCa implements ExecutionEnvironment {
  @Override
  public String testClassName() {
    return "unknown";
  }
  
  @Override
  public Optional<String> testSceneName() {
    return Optional.empty();
  }
  
  public static class ExecutionEnvironmentFactory implements AutotestExecution.Spec.Loader.ExecutionEnvironmentFactory<ExecutionEnvironmentForCa> {
    
    @Override
    public ExecutionEnvironmentForCa create() {
      return new ExecutionEnvironmentForCa();
    }
  }
}
