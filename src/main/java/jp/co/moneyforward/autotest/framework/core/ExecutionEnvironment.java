package jp.co.moneyforward.autotest.framework.core;

import java.util.Properties;

public interface ExecutionEnvironment {
  String endpointRoot();
  
  Credentials credentials();
  static ExecutionEnvironment load(Properties properties) {
    return new ExecutionEnvironment() {
      
      @Override
      public String endpointRoot() {
        return null;
      }
      
      @Override
      public Credentials credentials() {
        return null;
      }
    };
  }
}
