package jp.co.moneyforward.autotest.ca_web.accessmodels;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ElementState;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.microsoft.playwright.options.AriaRole.LINK;

/**
 * A utilitiy class for **caweb** application.
 */
public enum CawebUtils {
  ;
  
  /**
   * An action that hovers a specified sidebar item, then clicks a specified menu item.
   *
   * @param sidebarItemName A name of sidebar item to hover over.
   * @param menuItem        A name of a menu item which is shown when a mouse is hovering over the item specified by `sidebarItemName`.
   * @return An action that hovers an item specified by `sidebarItemName`, then clicks `menuItem`.
   */
  public static PageAct clickSidebarItem(final String sidebarItemName, final String menuItem) {
    return new PageAct(String.format("Click '%s'->'%s'", sidebarItemName, menuItem)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.getByText(sidebarItemName).hover();
        page.getByRole(LINK, new Page.GetByRoleOptions().setName(menuItem)).click();
      }
    };
  }
  
  /**
   * An action that clicks an element specified by `selector` and then fills a `value` in it.
   *
   * @param selector A selector string to specify an element in a `page`.
   * @param value    A value to be filled in into the selected item.
   * @return An action to perform the click and the text entry.
   */
  public static PageAct clickAndFill(String selector, String value) {
    return clickAndFill(String.format("Click '%s' and fill it with '%s'", selector, value),
                        selector,
                        value);
  }
  
  /**
   * An action that clicks an element specified by `selector` and then fills a `value` in it.
   *
   * @param description A description of the returned action.
   * @param selector    A selector string to specify an element in a `page`.
   * @param value       A value to be filled in into the selected item.
   * @return An action to perform the click and the text entry.
   */
  public static PageAct clickAndFill(final String description, String selector, String value) {
    return new PageAct(description) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator(selector).click();
        page.locator(selector).fill(value);
      }
    };
  }
  
  public static PageAct clickAndWaitForCompletion(final String elementTextToClick) {
    return clickAndWaitForCompletion(String.format("Click '%s' and wait for completion", elementTextToClick),
                                     elementTextToClick);
  }
  
  public static PageAct clickAndWaitForCompletion(final String description, final String elementTextToClick) {
    return new PageAct(description) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        // Let's make this a shared function.
        page.getByText(elementTextToClick, new Page.GetByTextOptions().setExact(true)).click();
        ElementHandle loader = page.querySelector(".ca-saving-cover");
        loader.waitForElementState(ElementState.HIDDEN);
      }
    };
  }
}
