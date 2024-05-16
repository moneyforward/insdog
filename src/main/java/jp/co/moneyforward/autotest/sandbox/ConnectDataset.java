package jp.co.moneyforward.autotest.sandbox;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class ConnectDataset implements Act<Object, Object> {
  
  @Override
  public Object perform(Object value, ExecutionEnvironment executionEnvironment) {
    return value;
  }
}
