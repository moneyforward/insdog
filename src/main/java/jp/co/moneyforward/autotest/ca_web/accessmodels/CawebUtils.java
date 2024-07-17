package jp.co.moneyforward.autotest.ca_web.accessmodels;

import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.microsoft.playwright.options.AriaRole.LINK;
import static com.microsoft.playwright.options.WaitForSelectorState.HIDDEN;

/**
 * A utility class for **caweb** application.
 */
public enum CawebUtils {
  ;
  
  /**
   * Returns an action that hovers a specified sidebar item, then clicks a specified menu item.
   *
   * @param menuItem        A name of a menu item which is shown when a mouse is hovering over the item specified by `sidebarItemName`.
   * @param sidebarItemName A name of sidebar item to hover over.
   * @return An action that hovers an item specified by `sidebarItemName`, then clicks `menuItem`.
   */
  public static PageAct navigateToMenuItemUnderSidebarItem(final String menuItem, final String sidebarItemName) {
    return new PageAct(String.format("Click '%s'->'%s'", sidebarItemName, menuItem)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.getByText(sidebarItemName).hover();
        page.getByRole(LINK, new Page.GetByRoleOptions().setName(menuItem)).click();
      }
    };
  }
  
  /**
   * Returns an action that clicks an element specified by `selector` and then fills a `value` in it.
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
  
  /**
   * Returns a `PageAct`, which clicks an element specified by the `elementTextToClick`.
   * It will also wait for its completion by making sure the element `.ca-saving-cover` is hidden.
   *
   * @param elementTextToClick A page
   * @return The page act that performs the behavior in the description.
   */
  public static PageAct clickAndWaitForCompletion(final String elementTextToClick) {
    return clickAndWaitForCompletion(String.format("Click '%s' and wait for completion", elementTextToClick),
                                     elementTextToClick);
  }
  
  private static PageAct clickAndWaitForCompletion(final String description, final String elementTextToClick) {
    return new PageAct(description) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        // Let's make this a shared function.
        page.getByText(elementTextToClick, new Page.GetByTextOptions().setExact(true)).nth(0).click();
        page.locator(".ca-saving-cover").waitFor(new WaitForOptions().setState(HIDDEN));
      }
    };
  }
}
