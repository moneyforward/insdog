package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.Playwright;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

public class OpenWindow implements LeafAct<Void, Playwright> {
  @Override
  public Playwright perform(Void value, ExecutionEnvironment executionEnvironment) {
    return Playwright.create();
  }
}
