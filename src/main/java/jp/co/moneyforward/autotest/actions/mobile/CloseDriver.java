package jp.co.moneyforward.autotest.actions.mobile;

import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.openqa.selenium.By;

public class CloseDriver implements Act<AppiumDriver, Void> {
  @Override
  public Void perform(AppiumDriver value, ExecutionEnvironment executionEnvironment) {
    value.quit();
    return null;
  }
}
