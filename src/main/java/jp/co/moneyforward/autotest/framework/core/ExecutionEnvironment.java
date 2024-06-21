package jp.co.moneyforward.autotest.framework.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.valid8j.classic.Requires.requireNonNull;

public interface ExecutionEnvironment {
  
  String PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY = "jp.co.moneyforward.autotest.testResultDirectory";
  
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
  
  default Path testResultDirectory() {
    return Paths.get(Utils.sanitize(System.getProperty(PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY, "target/testResult")),
                     Utils.sanitize(this.testClassName()),
                     Utils.sanitize(this.testSceneName().orElse("unknown-" + Utils.counter.getAndIncrement())));
  }
  
  default Path testOutputFilenameFor(String fileName) {
    return Paths.get(testResultDirectory().toString(), fileName);
  }
  
  enum Utils {
    ;
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    private static String sanitize(String pathName) {
      return pathName.replaceAll(":", "_")
                     .replaceAll("\\[", "")
                     .replaceAll("]", "");
    }
  }
}
