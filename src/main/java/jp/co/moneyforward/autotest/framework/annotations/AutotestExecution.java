package jp.co.moneyforward.autotest.framework.annotations;

import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
     * @return Names of methods to be executed in `@BeforeAll` phase.
     */
    String[] beforeAll() default {};
    
    /**
     * Returns names of methods to be executed in `@BeforEach` phase.
     * @return Names of methods to be executed in `@BeforeEach` phase.
     */
    String[] beforeEach() default {};
    
    /**
     * Returns names of methods to be executed in `@Test` phase.
     * @return Names of methods to be executed in `@Test` phase.
     */
    String[] value() default {};
    
    /**
     * Returns names of methods to be executed in `@AfterEach` phase.
     * @return Names of methods to be executed in `@AfterEach` phase.
     */
    String[] afterEach() default {};
    
    /**
     * Returns names of methods to be executed in `@AfterAll` phase.
     * @return Names of methods to be executed in `@AfterAll` phase.
     */
    String[] afterAll() default {};
    
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
       * This implementation of {@link Loader} reads the system property {@code jp.co.moneyforward.autotest} and creates an
       * {@link Spec} instance based on the value.
       * The syntax of the property is as follows:
       *
       * ```
       * value ::= inline:DEFINITION|file:FILENAME
       * DEFINITION ::= A JSON object whose known keys are `beforeAll`, `beforeEach`, `value`, `afterEach`, or `afterAll`.
       * FILENAME ::= {A name of a local file that holds the DEFINITION as its content}
       * ```
       *
       * If a known key is present, and it is an array containing only strings, it overrides the value from `base`.
       * Note that a value for the key in `base` will be completely ignored.
       * If a known key is present, but it is not an array or containing anything else than strings, an error will be reported.
       * Known keys in `DEFINITION` can be omitted.
       * Keys unknown to the loader are ignored, not reported as errors.
       *
       * If `file:` is specified in the property, a file specified by `FILENAME` will be read and its content will be
       * treated as `DEFINITION`.
       * The behavior will be exactly the same as the discussion above.
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
          // TODO
          return base;
        }
      }
      
      interface ExecutionEnvironmentFactory<E extends ExecutionEnvironment>  {
        E create();
      }
    }
  }
}
