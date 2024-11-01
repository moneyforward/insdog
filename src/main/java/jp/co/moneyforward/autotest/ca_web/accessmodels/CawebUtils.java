package jp.co.moneyforward.autotest.ca_web.accessmodels;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.WaitForOptions;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.microsoft.playwright.options.AriaRole.LINK;
import static com.microsoft.playwright.options.WaitForSelectorState.HIDDEN;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.materializeResource;

/**
 * A utility class for **caweb** application.
 */
public enum CawebUtils {
  ;
  
  /**
   * Returns an action that hovers a specified sidebar item, then clicks a specified menu item.
   *
   * **NOTE:** This can be
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
   * Returns an action that hovers a specified herder item, then clicks a specified menu item.
   *
   * @param menuItem A name of a menu item which is shown after clicking Office name on header item.
   * @param elementOfficeDropdownMenu A locator of office menu item to hover over.
   * @return An action that hovers an item specified by `officeMenuItemName`, then clicks `menuItem`.
   */
  public static PageAct navigateToMenuItemUnderOfficeSettingItem(final String menuItem, final String elementOfficeDropdownMenu) {
    return new PageAct(String.format("Click '%s'->'%s'", elementOfficeDropdownMenu, menuItem)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator(elementOfficeDropdownMenu).click();
        page.getByRole(LINK, new Page.GetByRoleOptions().setName(menuItem)).click();
      }
    };
  }
  
  /**
   * If click on the menu on the left to move to another page, PageAct performs
   * When moving to an external service
   *
   * @param menuItem Menu button name
   * @param menuSubItem Sub-menu button name related to the menu
   * @param pageAct PageAct after new page displays
   * @return The page act that performs the behavior in the description
   */
  public static PageAct navigateToNewTabUnderSidebarItemAndAct(final String menuItem, final String menuSubItem, PageAct pageAct) {
    return new PageAct(String.format("Click '%s'->'%s', and then act specified PageAct in new tab", menuItem, menuSubItem)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.getByText(menuItem).click();
        Page newPage = page.waitForPopup(()-> page.getByRole(LINK, new Page.GetByRoleOptions().setName(menuSubItem)).click());
        pageAct.perform(newPage, executionEnvironment);
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
  
  /**
   * Exporting data such as journal data, Click and select file type
   * Run PageAct after the file has been prepared
   *
   * @param locatorExportButton Buttons for selecting the data format, it is usually described as "エクスポート"
   * @param dataFormat name of data format, ex PDF出力
   * @param pageAct PageAct after export has started, ex Confirm file exporting status
   * @return The page act that performs the behavior in the description
   */
  public static PageAct exportDataSpecifiedFormat(final String locatorExportButton, final String dataFormat, PageAct pageAct) {
    return new PageAct(String.format("Click '%s'->'%s, and then act specified PageAct'", locatorExportButton, dataFormat)) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        page.locator(locatorExportButton).click();
        Page newPage = page.waitForPopup(()-> page.getByRole(LINK, new Page.GetByRoleOptions().setName(dataFormat)).click());
        
        pageAct.perform(newPage, executionEnvironment);
      }
    };
  }
  
  /**
   * Creating a departments 部門
   *
   * @param locatorButton Locator of the button that displays the form for creating departments
   * @param value Department name
   * @return The page act that performs the behavior in the description
   */
  public static PageAct clickButtonToDisplayModalAndEnterDepartmentNameAndRegister(final String locatorButton, final String value) {
    return new PageAct("create department: Display modal and enter value") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        Locator categoryFormModal = page.locator("#js-add-dept-modal");
        
        page.locator(locatorButton).first().click();
        
        page.waitForSelector("#js-add-dept-modal");
        
        if (categoryFormModal.isVisible()) {
          categoryFormModal.locator("#dept_name").fill(value);
          categoryFormModal.locator("#js-btn-add-dept").click();
          
        }
        categoryFormModal.locator("#btn-modal-close > img").click();
      }
    };
  }
}
