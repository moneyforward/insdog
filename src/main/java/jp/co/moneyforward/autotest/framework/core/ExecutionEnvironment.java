package jp.co.moneyforward.autotest.framework.core;

import java.util.Optional;
import java.util.Properties;

import static com.github.valid8j.classic.Requires.requireNonNull;

public interface ExecutionEnvironment {
  String testClassName();
  
  Optional<String> testSceneName();
  
  default ExecutionEnvironment withSceneName(String sceneName) {
    requireNonNull(sceneName);
    return new ExecutionEnvironment() {
      @Override
      public String testClassName() {
        return ExecutionEnvironment.this.testClassName();
      }
      
      @Override
      public Optional<String> testSceneName() {
        return Optional.of(sceneName);
      }
    };
  }
}
