package jp.co.moneyforward.autotest.ut.builtins;

import jp.co.moneyforward.autotest.framework.action.Wait;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.ut.framework.scene.SceneTest.createExecutionEnvironment;

public class WaitTest {
  @Test
  void givenWait_whenPerform() {
    var wait = new Wait<String>(1, TimeUnit.MICROSECONDS, "for unit test");
    
    wait.perform("whenValue", createExecutionEnvironment());
    
    assertStatement(value(wait).invoke("name")
                               .asString()
                               .toBe()
                               .containing("Wait")
                               .containing("1")
                               .containing("MICROSECONDS"));
  }
  
  @Test
  void givenWait_whenThreadInterrupted() throws InterruptedException {
    var wait = new Wait<String>(1, TimeUnit.SECONDS, "for unit test");
    Runnable runnable = () -> {
      long before = System.currentTimeMillis();
      wait.perform("whenValue", createExecutionEnvironment());
      long duration = System.currentTimeMillis() - before;
      assertAll(value(wait).invoke("name")
                           .asString()
                           .toBe()
                           .containing("Wait")
                           .containing("1")
                           .containing("SECONDS"),
                value(duration).toBe().lessThan(500L));
    };
    Thread thread = new Thread(runnable);
    thread.start();
    
    thread.interrupt();
    boolean wasInterrupted = thread.isInterrupted();
    thread.join();
    assertStatement(value(wasInterrupted).toBe().trueValue());
  }
}
