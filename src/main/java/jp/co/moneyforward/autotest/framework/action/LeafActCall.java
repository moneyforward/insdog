package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

public class LeafActCall<T, R> extends ActCall<T> {
  
  private final LeafAct<T, R> act;
  
  public LeafActCall(String outputFieldName, LeafAct<T, R> act, String inputFieldName) {
    super(inputFieldName, outputFieldName);
    this.act = act;
  }
  
  public LeafAct<T, R> act() {
    return this.act;
  }
  
  @Override
  public Action toAction(ActionComposer actionComposer, Map<String, Function<Context, Object>> assignmentResolversFromCurrentCall) {
    return actionComposer.create(this);
  }
}
