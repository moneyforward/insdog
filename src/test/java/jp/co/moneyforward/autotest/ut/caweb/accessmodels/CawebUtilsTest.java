package jp.co.moneyforward.autotest.ut.caweb.accessmodels;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.xml.sax.helpers.LocatorImpl;

import javax.management.relation.Role;

import static com.microsoft.playwright.options.AriaRole.LINK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CawebUtilsTest {
  @Test
  void whenNavigateToMenuItemUnderSidebarItem_thenMenuItemIsClicked() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Locator locator = Mockito.mock(Locator.class);
    Locator locatorToBeClicked = Mockito.mock(Locator.class);
    Page page = Mockito.mock(Page.class);
    when(page.getByText(any(String.class))).thenReturn(locator);
    when(page.getByRole(any(), any())).thenReturn(locatorToBeClicked);
    
    PageAct pageAct = CawebUtils.navigateToMenuItemUnderSidebarItem("Hello", "World");
    pageAct.perform(page, env);
    
    verify(locatorToBeClicked).click();
  }
  
  @Test
  void whenNavigateToNewTabUnderSidebarItemAndAct_thenPageActPerformed() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Locator menuItemLocator = Mockito.mock(Locator.class);
    Locator menuSubItemLocator = Mockito.mock(Locator.class);
    Page page = Mockito.mock(Page.class);
    Page newPage = Mockito.mock(Page.class);
    PageAct pageAct = Mockito.mock(PageAct.class);
    String menuItem = "Settings";
    String menuSubItem = "Profile";
    
    when(page.getByText(menuItem)).thenReturn(menuItemLocator);
    when(page.waitForPopup(any())).thenReturn(newPage);
    when(page.getByRole(LINK, new Page.GetByRoleOptions().setName(menuSubItem))).thenReturn(menuSubItemLocator);
    
    PageAct pageAct1 = CawebUtils.navigateToNewTabUnderSidebarItemAndAct(menuItem, menuSubItem, pageAct);
    pageAct1.perform(page, env);
    
    verify(menuItemLocator).click();
    verify(page).waitForPopup(any());
//    verify(menuSubItemLocator).click();
    verify(pageAct).perform(newPage, env);
  }
  
  @Test
  void whenClickAndFill_thenLocatorClickedAndFilled() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Locator locatorToBeClicked = Mockito.mock(Locator.class);
    Page page = Mockito.mock(Page.class);
    when(page.locator(any(String.class))).thenReturn(locatorToBeClicked);
    
    PageAct pageAct = CawebUtils.clickAndFill("DUMMY > SELECTOR > STRING>", "Hello, world!");
    pageAct.perform(page, env);
    
    verify(locatorToBeClicked).click();
    verify(locatorToBeClicked).fill("Hello, world!");
  }
  
  @Test
  void whenClickAndWaitForCompletion_thenLocatorClickedAndFilled() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Page page = Mockito.mock(Page.class);
    Locator locator = Mockito.mock(Locator.class);
    when(page.getByText(any(String.class), any())).thenReturn(locator);
    Locator locatorToBeClicked = Mockito.mock(Locator.class);
    when(locator.nth(0)).thenReturn(locatorToBeClicked);
    Locator locatorToBeWaitedOn = mock(Locator.class);
    when(page.locator(".ca-saving-cover")).thenReturn(locatorToBeWaitedOn);
    
    PageAct pageAct = CawebUtils.clickAndWaitForCompletion("helloElement");
    pageAct.perform(page, env);
    
    verify(locatorToBeClicked).click();
    verify(locatorToBeWaitedOn).waitFor(any());
  }
  
//  @Test
//  void whenElementIsEqualTo_thenElementIsEqualTo() {
//    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
//    Page page = Mockito.mock(Page.class);
//    Locator locator = Mockito.mock(Locator.class);
//    when(page.locator(any(String.class))).thenReturn(locator);
//
//    PageAct pageAct = CawebUtils.elementIsEqualTo("DUMMY > SELECTOR > STRING>", "Hello, world!");
//    pageAct.perform(page, env);
//
//    verify(locator).textContent().equals("Hello, world!");
//  }
  
  @Test
  void whenExportDataSpecifiedFormat_thenPageActPerformedInNewTab() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Page page = Mockito.mock(Page.class);
    Page newPage = Mockito.mock(Page.class);
    PageAct pageAct = Mockito.mock(PageAct.class);
    Locator locator = Mockito.mock(Locator.class);
    Locator fileFormatLocator = Mockito.mock(Locator.class);
    Role role = Mockito.mock(Role.class);
    
    String locatorExportButton = "#export-button";
    String dataFormat = "PDF";
    
    when(page.locator(locatorExportButton)).thenReturn(locator);
    when(page.getByRole(LINK, new Page.GetByRoleOptions().setName(dataFormat))).thenReturn(fileFormatLocator);
    when(page.waitForPopup(any())).thenReturn(newPage);
    
    PageAct pageAct1 = CawebUtils.exportDataSpecifiedFormat(locatorExportButton, dataFormat, pageAct);
    pageAct1.perform(page, env);
    
    verify(locator).click();
    verify(page).waitForPopup(any());
//    verify(fileFormatLocator).click();
    verify(pageAct).perform(newPage, env);
  }
  
  @Test
  void whenClickButtonToDisplayModalAndEnterDepartmentNameAndRegister_thenModalDisplayedAndDepartmentRegistered() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Page page = Mockito.mock(Page.class);
    Locator buttonLocator = Mockito.mock(Locator.class);
    Locator modalLocator = Mockito.mock(Locator.class);
    Locator deptNameLocator = Mockito.mock(Locator.class);
    Locator addDeptButtonLocator = Mockito.mock(Locator.class);
    Locator closeModalButtonLocator = Mockito.mock(Locator.class);
    
    String locatorButton = "#add-dept-button";
    String value = "New Department";
    
    when(page.locator(locatorButton)).thenReturn(buttonLocator);
    when(buttonLocator.first()).thenReturn(buttonLocator);
    when(page.locator("#js-add-dept-modal")).thenReturn(modalLocator);
    when(modalLocator.isVisible()).thenReturn(true);
    when(modalLocator.locator("#dept_name")).thenReturn(deptNameLocator);
    when(modalLocator.locator("#js-btn-add-dept")).thenReturn(addDeptButtonLocator);
    when(modalLocator.locator("#btn-modal-close > img")).thenReturn(closeModalButtonLocator);
    
    PageAct pageAct = CawebUtils.clickButtonToDisplayModalAndEnterDepartmentNameAndRegister(locatorButton, value);
    pageAct.perform(page, env);
    
    verify(buttonLocator).click();
    verify(page).waitForSelector("#js-add-dept-modal");
    verify(modalLocator).isVisible();
    verify(deptNameLocator).fill(value);
    verify(addDeptButtonLocator).click();
    verify(closeModalButtonLocator).click();
  }
}
