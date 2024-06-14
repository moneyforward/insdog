package jp.co.moneyforward.autotest.actions.web;

import com.github.dakusui.actionunit.actions.Leaf;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.net.URI;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.valid8j.classic.Requires.requireNonNull;

public class Screenshot implements LeafAct<Page, Page> {
  private static final AtomicInteger counter = new AtomicInteger(0);
  private final String path;
  
  public Screenshot(String path) {
    this.path = requireNonNull(path);
  }
  
  @Override
  public Page perform(Page value, ExecutionEnvironment executionEnvironment) {
    value.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(this.path,
                                                                    executionEnvironment.testClassName(),
                                                                    executionEnvironment.testSceneName()
                                                                                        .orElse("unknown-" + counter.getAndIncrement())
                                                                        + ".png")));
    return value;
  }
}
