package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.valid8j.pcond.fluent.Statement;

import java.util.function.Function;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;

public interface ActionFactory<T, R> {
  
  default String name() {
    return this.getClass().getSimpleName();
  }
  
  default Action toAction(ActionComposer actionComposer, String inputFieldName, String outputFieldName) {
    return actionComposer.create(this, inputFieldName, outputFieldName);
  }
}
