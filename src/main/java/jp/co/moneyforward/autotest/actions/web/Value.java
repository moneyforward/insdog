package jp.co.moneyforward.autotest.actions.web;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class Value<V> implements Act<V, V> {
  @Override
  public V perform(V value, ExecutionEnvironment executionEnvironment) {
    return value;
  }
}
