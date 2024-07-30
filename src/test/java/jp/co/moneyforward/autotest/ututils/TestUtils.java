package jp.co.moneyforward.autotest.ututils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

public enum TestUtils {
  ;
  
  static final PrintStream STDOUT = System.out;
  static final PrintStream STDERR = System.err;
  public static final PrintStream NOP = new PrintStream(new OutputStream() {
    @Override
    public void write(int b) {
    }
  });
  
  /**
   * Typically called from a method annotated with {@literal @}{@code Before} method.
   */
  public static void suppressStdOutErrIfUnderPitestOrSurefire() {
    if (InternalUtils.isRunUnderPitest() || InternalUtils.isRunUnderSurefire()) {
      System.setOut(NOP);
      System.setErr(NOP);
    }
  }
  
  /**
   * Typically called from a method annotated with {@literal @}{@code After} method.
   */
  public static void restoreStdOutErr() {
    System.setOut(STDOUT);
    System.setErr(STDERR);
  }
  
  public static Browser launchHeadlessBrowser(BrowserType browserType) {
    return browserType.launch(new BrowserType.LaunchOptions().setHeadless(true));
  }
  
  public static String testTableResourcePath(String pathToTestResourceFile) {
    return "file://" + new File(new File(System.getProperty("user.dir")), "src/test/resources/" + pathToTestResourceFile).getAbsolutePath();
  }
}
