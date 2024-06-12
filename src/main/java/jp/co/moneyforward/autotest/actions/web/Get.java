package jp.co.moneyforward.autotest.actions.web;

import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Get implements LeafAct<Object, Object> {
  private final String url;
  
  public Get(String url) {
    this.url = url;
  }
  @Override
  public Object perform(Object value, ExecutionEnvironment executionEnvironment) {
    return this.url;
  }
}
