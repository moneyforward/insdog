package jp.co.moneyforward.autotest.sandbox;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
          page.focus("input[name='mfid_user[email]']");
          page.keyboard().type("ukai.hiroshi@moneyforward.co.jp");
          page.click("button[id='submitto']");
          page.focus("input[name='mfid_user[password]']");
          page.keyboard().type("!QAZ@WSX");
          page.click("button[id='submitto']");
          page.click("text=スキップする");
          //Thread.sleep(1000);
          //page.waitForSelector("button[id='submitto']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
          //page.click("button[id='submitto']");
          //Thread.sleep(1000);
        } finally {
          page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
        }
      }
    }
    
    System.out.println(new PlaywrightSandbox().process("hello"));
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
