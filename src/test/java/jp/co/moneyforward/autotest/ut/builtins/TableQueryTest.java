package jp.co.moneyforward.autotest.ut.builtins;

import com.microsoft.playwright.*;
import jp.co.moneyforward.autotest.actions.web.TableQuery;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.actions.web.TableQuery.Term.term;
import static jp.co.moneyforward.autotest.ututils.TestUtils.launchHeadlessBrowser;

public class TableQueryTest extends TestBase {
  @Test
  void givenCawebOfficeListTable_whenPerformQueryResultingInMultipleRows_thenExpectedLocatorsReturned() {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = launchHeadlessBrowser(playwright.chromium())) {
        Page page = browser.newPage();
        page.navigate(testTableResourcePath());
        //#js-ca-main-contents > table > thead
        
        List<Locator> locators = TableQuery.select("事業者・年度の切替")
                                           .from("body > table")
                                           .where(term("事業者名", "abc-154206"))
                                           .normalizeWith(normalizerFunction())
                                           .build()
                                           .perform(page);
        
        assertAll(value(locators).size().toBe().greaterThan(1),
                  value(locators).invoke("getFirst")
                                 .invoke("getByText", "切替")
                                 .invoke("textContent")
                                 .invoke("trim")
                                 .toBe()
                                 .equalTo("切替"));
      }
    }
  }
  
  @Test
  void givenCawebOfficeListTable_whenPerformQueryResultingInSingleRow_thenExpectedLocatorsReturned2() {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = launchHeadlessBrowser(playwright.chromium())) {
        Page page = browser.newPage();
        page.navigate(testTableResourcePath());
        //#js-ca-main-contents > table > thead
        
        List<Locator> locators = TableQuery.select("事業者・年度の切替")
                                           .from("body > table")
                                           .where(term("事業者名", "abc-154206"),
                                                  term("会計年度", "次年度"))
                                           .normalizeWith(normalizerFunction())
                                           .build()
                                           .perform(page);
        
        assertAll(value(locators).size().toBe().equalTo(1),
                  value(locators)
                      .invoke("getFirst")
                      .invoke("getByText", "切替")
                      .invoke("textContent")
                      .invoke("trim")
                      .toBe()
                      .equalTo("切替"));
      }
    }
  }
  
  @Test
  void givenCawebOfficeListTable_whenPerformQueryForNonExistingOfficeName_thenExpectedLocatorsReturned() {
    try (Playwright playwright = Playwright.create()) {
      try (Browser browser = launchHeadlessBrowser(playwright.chromium())) {
        Page page = browser.newPage();
        page.navigate(testTableResourcePath());
        //#js-ca-main-contents > table > thead
        
        List<Locator> result = TableQuery.select("事業者・年度の切替")
                                         .from("body > table")
                                         .where(term("事業者名", "abc-999999"))
                                         .normalizeWith(normalizerFunction())
                                         .build()
                                         .perform(page);
        
        assertStatement(value(result).toBe().empty());
      }
    }
  }
  
  public static BinaryOperator<List<Locator>> normalizerFunction() {
    return (lastCompleteRow, incompleteRow) -> {
      List<Locator> ret = new ArrayList<>(lastCompleteRow.size());
      for (int i = 0; i < lastCompleteRow.size(); i++) {
        int offset = lastCompleteRow.size() - incompleteRow.size() - 1;
        ret.add((i < offset || i == lastCompleteRow.size() - 1) ? lastCompleteRow.get(i)
                                                                : incompleteRow.get(i - (offset)));
      }
      return ret;
    };
  }
  
  private static String testTableResourcePath() {
    return "file://" + new File(new File(System.getProperty("user.dir")), "src/test/resources/caweb/testTable.html").getAbsolutePath();
  }
}
