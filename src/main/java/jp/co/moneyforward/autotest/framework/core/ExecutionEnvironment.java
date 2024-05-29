package jp.co.moneyforward.autotest.framework.core;

import java.util.Properties;

public interface ExecutionEnvironment {
  static ExecutionEnvironment load(Properties properties) {
    return new ExecutionEnvironment() {
    };
  }
}
