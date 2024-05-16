package jp.co.moneyforward.autotest.ca_web.actions.gui;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.annotations.RegisterAsAction;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

@RegisterAsAction
public class Click implements Act<Object, Object> {
  private final String locatorString;
  
  public Click(String locatorString) {
    this.locatorString = locatorString;
  }
  @Override
  public Object perform(Object value, ExecutionEnvironment executionEnvironment) {
    System.out.println(this.locatorString);
    return value;
  }
}
