package jp.co.moneyforward.autotest.ca_web.actions.gui;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Get implements Act<Object, Object> {
  private final String url;
  
  public Get(String url) {
    this.url = url;
  }
  @Override
  public Object perform(Object value, ExecutionEnvironment executionEnvironment) {
    return this.url;
  }
}
