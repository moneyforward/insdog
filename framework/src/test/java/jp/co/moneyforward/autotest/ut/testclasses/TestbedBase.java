package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeEach = {"login"},
        value = {"connect", "disconnect"},
        afterEach = {"logout"}))
public abstract class TestbedBase implements AutotestRunner {
  @Named
  public static Scene login() {
    return new Scene.Builder("login").build();
  }
  
  @Named
  public static Scene connect() {
    return new Scene.Builder("connect").build();
  }
  
  @Named
  public static Scene disconnect() {
    return new Scene.Builder("disconnect").build();
  }
  
  @Named
  public static Scene logout() {
    return new Scene.Builder("logout").build();
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return ReportingActionPerformer.create();
  }
}
