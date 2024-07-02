package jp.co.moneyforward.autotest.framework.annotations;

import com.github.valid8j.fluent.Expectations;
import jp.co.moneyforward.autotest.framework.cli.CliBase;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.*;

import static com.github.valid8j.fluent.Expectations.require;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.PASSTHROUGH;

/**
 * An annotation to let JUnit5 know the class to which this is attached is a test class to be executed by {@link AutotestEngine}
 * extension.
 */
@Retention(RUNTIME)
@ExtendWith(AutotestEngine.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public @interface AutotestExecution {
  
  /**
   * Specifies a class for loading a {@link Spec} instance.
   *
   * @return A loader class.
   * @see Spec.Loader
   */
  Class<? extends Spec.Loader> executionSpecLoaderClass() default Spec.Loader.Default.class;
  
  /**
   * Specifies how to execute a test class to which this annotation is attached by default.
   *
   * @return An instance that specifies how to execute the test class.
   * @see Spec
   */
  Spec defaultExecution() default @Spec;
  
  /**
   * For each attribute in this annotation interface, user can specify names of `public`, `static`, and `@Named` methods that
   * return `Scene` instance in the test class.
   *
   * @see Named
   */
  @interface Spec {
    /**
     * Returns names of methods to be executed in `@BeforeAll` phase.
     *
     * @return Names of methods to be executed in `@BeforeAll` phase.
     */
    String[] beforeAll() default {};
    
    /**
     * Returns names of methods to be executed in `@BeforEach` phase.
     *
     * @return Names of methods to be executed in `@BeforeEach` phase.
     */
    String[] beforeEach() default {};
    
    /**
     * Returns names of methods to be executed in `@Test` phase.
     *
     * @return Names of methods to be executed in `@Test` phase.
     */
    String[] value() default {};
    
    /**
     * Returns names of methods to be executed in `@AfterEach` phase.
     *
     * @return Names of methods to be executed in `@AfterEach` phase.
     */
    String[] afterEach() default {};
    
    /**
     * Returns names of methods to be executed in `@AfterAll` phase.
     *
     * @return Names of methods to be executed in `@AfterAll` phase.
     */
    String[] afterAll() default {};
    
    /**
     * Resolves the dependencies automatically.
     *
     * If enabled, scenes depended on by scenes specified in `value` are automatically included in `beforeAll` at execution.
     *
     * Note that, scenes depended on by ones in `value` but depending on another in `value` will be included in `value` not in `beforeAll` at execution.
     *
     * @return `PASSTHROUGH` execute actions as specified / `DEPENDENCY_BASED_DEFAULT` execute actions based on dependency.
     * @see PlanningStrategy
     */
    PlanningStrategy planExecutionWith() default PASSTHROUGH;
    
    /**
     * An interface to be implemented by custom loader class for {@link Spec} instance.
     * An implementation of this interface must have a public constructor without parameter to be used as
     * {@link AutotestExecution#executionSpecLoaderClass()}.
     */
    interface Loader {
      /**
       * Loads an instance of {@link Spec}.
       *
       * @param base A base `Spec` instance on which an implementation of this method loads execution spec.
       * @see Default
       */
      Spec load(Spec base);
      
      /**
       * This implementation of {@link Loader} reads the system property {@code jp.co.moneyforward.autotest.scenes} and creates a
       * {@link Spec} instance based on the value.
       * The syntax of the property value (`PROPERTY_VALUE`) is as follows:
       *
       * ```
       * PROPERTY_VALUE ::= inline:KEY=VALUE(,VALUE)*(;KEY=VALUE(,VALUE)*)*
       * KEY            ::= 'beforeAll'|'beforeEach'|'tests'|'afterEach'|'afterAll'
       * VALUE          ::= a defined name of a scene method.
       * ```
       *
       * @see CliBase
       */
      class Default implements Loader {
        /**
         * Returns a new {@code Spec} instance based on the specification discussed in {@link Default}.
         *
         * @param base A base `Spec` instance on which an implementation of this method loads execution spec.
         * @return A new {@link Spec} object.
         */
        @Override
        public Spec load(Spec base) {
          return parseProperties(System.getProperties(), base);
        }
        
        private static Spec parseProperties(Properties properties, Spec base) {
          Optional<PlanningStrategy> resolveDependencies = parseResolveDependenciesProperty(properties);
          Map<String, List<String>> customScenesMap = parseScenesProperty(properties);
          return new Spec() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
              return Spec.class;
            }
            
            @Override
            public String[] beforeAll() {
              return customScenesMap.containsKey("beforeAll") ? customScenesMap.get("beforeAll").toArray(new String[0])
                                                              : base.beforeAll();
            }
            
            @Override
            public String[] beforeEach() {
              return customScenesMap.containsKey("beforeEach") ? customScenesMap.get("beforeEach").toArray(new String[0])
                                                               : base.beforeEach();
            }
            
            @Override
            public String[] value() {
              return customScenesMap.containsKey("value") ? customScenesMap.get("value").toArray(new String[0])
                                                          : base.value();
            }
            
            @Override
            public String[] afterEach() {
              return customScenesMap.containsKey("afterEach") ? customScenesMap.get("afterEach").toArray(new String[0])
                                                              : base.afterEach();
            }
            
            @Override
            public String[] afterAll() {
              return customScenesMap.containsKey("afterAll") ? customScenesMap.get("afterAll").toArray(new String[0])
                                                             : base.afterAll();
            }
            
            @Override
            public PlanningStrategy planExecutionWith() {
              return resolveDependencies.orElse(base.planExecutionWith());
            }
          };
        }
        
        private static Optional<PlanningStrategy> parseResolveDependenciesProperty(Properties properties) {
          String propertyKeyForResolveDependencies = "jp.co.moneyforwaed.autotest.resolveDependencies";
          if (!properties.containsKey(propertyKeyForResolveDependencies)) {
            return Optional.empty();
          }
          return Optional.of(Boolean.parseBoolean(properties.getProperty(propertyKeyForResolveDependencies)) ? DEPENDENCY_BASED
                                                                                                             : PASSTHROUGH);
        }
        
        private static Map<String, List<String>> parseScenesProperty(Properties properties) {
          if (!properties.contains("jp.co.moneyforward.autotest.scenes"))
            return Map.of();
          String value = properties.getProperty("jp.co.moneyforward.autotest.scenes");
          String body = require(Expectations.value(value).toBe().startingWith("inline:")).substring("inline:".length());
          
          String[] entries = body.split(";");
          Map<String, List<String>> parsed = new HashMap<>();
          List<String> errors = new ArrayList<>();
          for (String entry : entries) {
            String[] keyAndValues = entry.split("=");
            String key = keyAndValues[0];
            String[] values = keyAndValues[1].split(",");
            if (Set.of("beforeAll", "beforeEach", "value", "afterEach", "afterAll").contains(key)) {
              parsed.put(key, List.of(values[0]));
            } else {
              errors.add("Unknown key: " + key);
            }
          }
          if (!errors.isEmpty())
            throw new IllegalArgumentException("Unknown keywords are found: " + errors);
          return parsed;
        }
      }
      
      interface ExecutionEnvironmentFactory<E extends ExecutionEnvironment> {
        E create();
      }
    }
  }
}
