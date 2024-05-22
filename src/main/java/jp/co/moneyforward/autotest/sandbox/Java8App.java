package jp.co.moneyforward.autotest.sandbox;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 * An entry-point class of Java8 Template.
 */
public class Java8App {
  public Java8App() {
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
   *     :                         ^
   *     |       Lots of work      |
   *     +-------------------------+
   * ----
   *z
   * Have fun!
   *
   * @param args Arguments passed through the command line.
   */
  public static void main(String... args) {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = playwright.chromium().launch()) {
        Page page = browser.newPage();
        page.navigate("https://ca-web-ca-app-ai-ocr-bulk-upload.idev.test.musubu.co.in/voucher_journals/journal_candidates");
        System.out.println(page.title());
        page.focus("input[name='mfid_user[email]']");
        page.keyboard().type("ukai.hiroshi@moneyforward.co.jp");
        page.focus("input[name='mfid_user[password]']");
        page.keyboard().type("password!");
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
      }
    }
    
    System.out.println(new Java8App().process("hello"));
  }
  
  /**
   * A processing method.
   * @param s A string to be processed.
   * @return A processed string.
   */
  public String process(String s) {
    return "processed:" + s;
  }
}
