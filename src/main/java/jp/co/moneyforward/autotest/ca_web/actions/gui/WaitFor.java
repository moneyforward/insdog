package jp.co.moneyforward.autotest.ca_web.actions.gui;


import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.annotations.RegisterAsAction;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

@RegisterAsAction
public class WaitFor implements Act<Object, Object> {
  @Override
  public Object perform(Object value, ExecutionEnvironment executionEnvironment) {
    return null;

  }
}
