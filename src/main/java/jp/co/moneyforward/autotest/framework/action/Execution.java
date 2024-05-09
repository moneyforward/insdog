package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.List;

public interface Execution {
  Action beforeAll();
  
  Action beforeEach();
  
  List<Action> main();
  
  Action afterEach();
  
  Action afterAll();
  
}
