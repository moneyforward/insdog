package jp.co.moneyforward.autotest.examples;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Paths;

/**
 * An entry-point class of Java8 Template.
 */
public class PlaywrightSandbox {
  public PlaywrightSandbox() {
  }
  
  /**
   * This is an entry-point class of the Java 8 example project.
   *
   * [ditaa]
   * .Ditaa diagram example
   * ----
   * +--------+   +-------+    +-------+
   * |        +---+ ditaa +--> |       |
   * |  Text  |   +-------+    |diagram|
   * |Document|   |!magic!|    |       |
   * |     {d}|   |       |    |       |
   * +---+----+   +-------+    +-------+
   * :                         ^
   * |       Lots of work      |
   * +-------------------------+
   * ----
   *
   * Have fun!
   *
   * @param args Arguments passed through the command line.
   */
  public static void main(String... args) {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = playwright.chromium().launch()) {
        Page page = browser.newPage();
        try {
          //page.navigate("https://ca-web-ca-app-ai-ocr-bulk-upload.idev.test.musubu.co.in/voucher_journals/journal_candidates");
          page.navigate("https://ca-web-ca-app-ai-ocr-bulk-upload.idev.test.musubu.co.in");
          System.out.println(page.title());
          page.click("text=ログインはこちらから");
          page.focus("input[name='mfid_user[email]']");
          page.keyboard().type("ukai.hiroshi@moneyforward.co.jp");
          page.click("button[id='submitto']");
          page.focus("input[name='mfid_user[password]']");
          page.keyboard().type("!QAZ@WSX");
          page.click("button[id='submitto']");
          //var x = page.getByRole(null, new Page.GetByRoleOptions().set);
          page.click("text=データ連携");
          page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("新規登録").setExact(true)).click();
          page.click("text=【法人】楽天銀行");
          
          //page.click("click,#tab1 > ul.account-list > li:nth-child(1) > a");
          //page.click("text=データ連携");
          //page.click("text=新規登録");
          //page.click("text=【法人】楽天銀行");
          //Thread.sleep(1000);
          //page.waitForSelector("button[id='submitto']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
          //page.click("button[id='submitto']");
          //Thread.sleep(1000);
        } finally {
          page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("target/example.png")));
        }
      }
    }
  }
  
  /**
   * A processing method.
   *
   * @param s A string to be processed.
   * @return A processed string.
   */
  public String process(String s) {
    return "processed:" + s;
  }
}
