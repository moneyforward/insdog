package jp.co.moneyforward.autotest.framework.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * An interface to model the execution environment.
 */
public interface ExecutionEnvironment {
  
  String PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY = "jp.co.moneyforward.autotest.testResultDirectory";
  
  String testClassName();
  
  Optional<String> testSceneName();
  
  String stepName();
  
  default ExecutionEnvironment withDisplayName(String displayName, String stageName) {
    requireNonNull(displayName);
    return new ExecutionEnvironment() {
      @Override
      public String testClassName() {
        return ExecutionEnvironment.this.testClassName();
      }
      
      @Override
      public Optional<String> testSceneName() {
        return Optional.of(displayName);
      }
      
      @Override
      public String stepName() {
        return stageName;
      }
    };
  }
  
  default Path testResultDirectory() {
    return testResultDirectoryFor(this.testClassName(),
                                  this.testSceneName().orElse("unknown-" + Utils.counter.getAndIncrement()));
  }
  
  static Path testResultDirectoryFor(String testClassName, String displayName) {
    return testResultDirectory(baseLogDirectoryForTestSession(),
                               Utils.sanitize(testClassName),
                               Utils.sanitize(displayName));
  }
  
  static Path testResultDirectory(String baseLogDirectoryForTestSession, String... dirs) {
    return Paths.get(baseLogDirectoryForTestSession, dirs);
  }
  
  static String baseLogDirectoryForTestSession() {
    return Utils.sanitize(System.getProperty(PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY, "target/testResult"));
  }
  
  default Path testOutputFilenameFor(String fileName) {
    return Paths.get(testResultDirectory().toString(), fileName);
  }
  
  enum Utils {
    ;
    private static final AtomicInteger counter = new AtomicInteger(0);
    
    private static String sanitize(String pathName) {
      return pathName.replace(":", "_")
                     .replace("[", "")
                     .replace("]", "");
    }
  }
}
