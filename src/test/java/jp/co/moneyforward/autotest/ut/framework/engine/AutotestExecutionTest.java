package jp.co.moneyforward.autotest.ut.framework.engine;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Properties;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AutotestExecutionTest extends TestBase {
  @Test
  void givenEmptyProperties_whenLoaderDefault() {
    AutotestExecution.Spec spec = new AutotestExecution.Spec.Loader.Default().load(baseSpec(), new Properties());
    
    assertStatement(value(specToString(spec)).toBe()
                                             .containing("BEFORE_ALL:[]")
                                             .containing("BEFORE_EACH:[]")
                                             .containing("TESTS:[]")
                                             .containing("AFTER_EACH:[]")
                                             .containing("AFTER_ALL:[]"));
  }
  
  @Test
  void givenValidNonEmptyProperties_whenLoaderDefault() {
    Properties properties = new Properties();
    properties.put(
        "jp.co.moneyforward.autotest.scenes",
        "inline:" +
            "beforeAll=open;" +
            "beforeEach=login;" +
            "value=test1,test2;" +
            "afterEach=logout,logout;" +
            "afterAll=");
    
    AutotestExecution.Spec spec = new AutotestExecution.Spec.Loader.Default().load(baseSpec(), properties);
    
    assertStatement(value(specToString(spec)).toBe()
                                             .containing("BEFORE_ALL:[open]")
                                             .containing("BEFORE_EACH:[login]")
                                             .containing("TESTS:[test1, test2]")
                                             .containing("AFTER_EACH:[logout, logout]")
                                             .containing("AFTER_ALL:[]")
    );
  }
  
  @Test
  void givenInvalidNonEmptyProperties$undefinedStageName$noEqualSign_whenLoaderDefault_thenIllegalArgumentException() {
    Properties properties = new Properties();
    properties.put(
        "jp.co.moneyforward.autotest.scenes",
        "inline:" +
            "notDefinedStageName=open;" +
            "entryWithoutEqual:login,login");
    assertThrows(IllegalArgumentException.class,
                 () -> {
                   try {
                     new AutotestExecution.Spec.Loader.Default().load(baseSpec(), properties);
                   } catch (IllegalArgumentException e) {
                     assertStatement(value(e.getMessage()).toBe()
                                                          .containing("Unknown stage name")
                                                          .containing("notDefinedStageName")
                                                          .containing("'='")
                                                          .containing("entryWithoutEqual:"));
                     throw e;
                   }
                 });
  }
  
  private static String specToString(AutotestExecution.Spec spec) {
    return "BEFORE_ALL:" +
        Arrays.toString(spec.beforeAll()) +
        String.format("%n") +
        "BEFORE_EACH:" +
        Arrays.toString(spec.beforeEach()) +
        String.format("%n") +
        "TESTS:" +
        Arrays.toString(spec.value()) +
        String.format("%n") +
        "AFTER_EACH:" +
        Arrays.toString(spec.afterEach()) +
        String.format("%n") +
        "AFTER_ALL:" +
        Arrays.toString(spec.afterAll()) +
        String.format("%n");
  }
  
  private static AutotestExecution.Spec baseSpec() {
    return new AutotestExecution.Spec() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return AutotestExecution.Spec.class;
      }
      
      @Override
      public String[] beforeAll() {
        return new String[0];
      }
      
      @Override
      public String[] beforeEach() {
        return new String[0];
      }
      
      @Override
      public String[] value() {
        return new String[0];
      }
      
      @Override
      public String[] afterEach() {
        return new String[0];
      }
      
      @Override
      public String[] afterAll() {
        return new String[0];
      }
      
      @Override
      public PlanningStrategy planExecutionWith() {
        return PlanningStrategy.PASSTHROUGH;
      }
    };
  }
}
