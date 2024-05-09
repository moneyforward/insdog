package jp.co.moneyforward.autotest.framework.core;

import java.util.Properties;

public interface ExecutionEnvironment {
  String endpointRoot();
  
  Credentials credentials();
  static ExecutionEnvironment load(Properties properties) {
    throw new UnsupportedOperationException();
  }
}
