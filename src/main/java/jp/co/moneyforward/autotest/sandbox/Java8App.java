package jp.co.moneyforward.autotest.sandbox;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.nio.file.Paths;

/**
 * An entry-point class of Java8 Template.
 */
public class Java8App {
  Java8App() {
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
   *
   * Have fun!
   *
   * @param args Arguments passed through the command line.
   */
  public static void main(String... args) {
    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.chromium().launch();
      Page page = browser.newPage();
      page.navigate("http://www.google.com");
      System.out.println(page.title());
      page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("example.png")));
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
