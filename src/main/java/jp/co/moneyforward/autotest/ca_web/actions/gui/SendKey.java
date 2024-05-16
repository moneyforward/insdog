package jp.co.moneyforward.autotest.ca_web.actions.gui;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.action.ActionFactory;
import jp.co.moneyforward.autotest.framework.annotations.RegisterAsAction;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

@RegisterAsAction
public class SendKey implements ActionFactory<String, String> {
  @Override
  public Action toAction(Function<Context, Io<String, String>> ioProvider, ExecutionEnvironment executionEnvironment) {
    return null;
  }
  
  @Override
  public String name() {
    return null;
  }
}
