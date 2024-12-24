package jp.co.moneyforward.autotest.framework.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static com.github.valid8j.fluent.Expectations.precondition;
import static com.github.valid8j.fluent.Expectations.value;

/// 
/// An interface to model the execution environment.
/// 
public interface ExecutionEnvironment {
  
  /// 
  /// A system property key for a base directory under which test results are stored.
  /// 
  String PROPERTY_KEY_FOR_TEST_RESULT_DIRECTORY = "jp.co.moneyforward.autotest.testResultDirectory";
  
  /// 
  /// A currently ongoing test class name.
  /// 
  /// @return A test class name.
  /// 
  String testClassName();
  
  /// 
  /// Returns a name of a currently ongoing scene, if any.
  /// 
  /// @return A name of an ongoing scene.
  /// @see ExecutionEnvironment#withDisplayName(String, String)
  /// 
  Optional<String> testSceneName();
  
  /// 
  /// A name of an ongoing step, such as `beforeAll`, `afterEach`, or `main`.
  /// 
  /// @return A name of an ongoing step.
  /// @see ExecutionEnvironment#withDisplayName(String, String)
  /// 
  String stepName();
  
  /// 
  /// Returns a new {@link ExecutionEnvironment} instance, with a currently ongoing display name and a step name.
  /// 
  /// @param displayName A display name of a currently ongoing scene.
  /// @param stageName   A stage name of a currently ongoing scene.
  /// @return A new execution environment object.
  /// 
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
  
  /// 
  /// Returns a `Path` to a directory under which test results are stored.
  /// 
  /// @return A path to a test result directory.
  /// 
  default Path testResultDirectory() {
    return testResultDirectoryFor(this.testClassName(),
                                  this.testSceneName().orElse("unknown-" + Utils.counter.getAndIncrement()));
  }
  
  /// 
  /// Returns an absolute path to a test result file.
  /// 
  /// @param fileName A file name under the test result directory.
  /// @return An absolute path to a test result file.
  /// 
  default Path testOutputFilenameFor(String fileName) {
    return Paths.get(testResultDirectory().toString(), fileName);
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
  
  default Path testOutputFilenameFor(String fileStem, String fileExtension) {
    return testOutputFilenameFor(fileStem, fileExtension, 0);
  }
  
  private Path testOutputFilenameFor(String fileStem, String fileExtension, int index) {
    assert precondition(value(index).toBe().greaterThanOrEqualTo(0));
    Path path;
    if (index == 0)
      path = testOutputFilenameFor(baseName(fileStem, fileExtension));
    else
      path = testOutputFilenameFor(baseName(fileStem + "-" + index, fileExtension));
    if (!path.toFile().exists())
      return path;
    return testOutputFilenameFor(fileStem, fileExtension, index + 1);
  }
  
  private static String baseName(String fileStem, String fileExtension) {
    return String.format("%s.%s", fileStem, fileExtension);
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
