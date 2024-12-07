package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.nio.file.Paths;

public class StoreStorageState implements Act<Page, Page> {
  private final String filePath;
  
  public StoreStorageState(String filePath) {
    this.filePath = filePath;
  }
  
  @Override
  public Page perform(Page page, ExecutionEnvironment executionEnvironment) {
    page.context().storageState(new BrowserContext.StorageStateOptions().setPath(Paths.get(filePath)));
    return page;
  }
}
