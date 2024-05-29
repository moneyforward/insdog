package jp.co.moneyforward.autotest.ca_web.core;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class ExecutionEnvironmentForCa implements ExecutionEnvironment {
  public String endpointRoot() {
    return null;
  }
  
  public Credentials credentials() {
    return null;
  }
  
  public static class ExecutionEnvironmentFactory implements AutotestExecution.Spec.Loader.ExecutionEnvironmentFactory<ExecutionEnvironmentForCa> {
    
    @Override
    public ExecutionEnvironmentForCa create() {
      return new ExecutionEnvironmentForCa();
    }
  }
}
