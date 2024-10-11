package jp.co.moneyforward.autotest.ut.caweb.accessmodels;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
  
  @Test
  void whenNavigateToMenuItemUnderOfficeSettingItem_thenMenuItemIsClicked() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Page page = Mockito.mock(Page.class);
    Locator locator = Mockito.mock(Locator.class);
    Locator locatorToBeClicked = Mockito.mock(Locator.class);
    when(page.locator(any(String.class))).thenReturn(locator);
    when(page.getByRole(any(), any())).thenReturn(locatorToBeClicked);
    
    PageAct pageAct = CawebUtils.navigateToMenuItemUnderOfficeSettingItem("Hello", "World");
    pageAct.perform(page, env);
    
    verify(locator).click();
    verify(locatorToBeClicked).click();
  }
  
  @Test
  void whenNavigateToNewTabUnderSidebarItemAndAct_thenMenuItemIsClickedAndActPageAct() {
    ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    Page page = Mockito.mock(Page.class);
    Locator locator = Mockito.mock(Locator.class);
    Locator locatorToBeClicked = Mockito.mock(Locator.class);
    PageAct pageActForNewTab = Mockito.mock(PageAct.class);
    Page newPage = Mockito.mock(Page.class);
    when(page.getByText(any(String.class))).thenReturn(locator);
    when(page.getByRole(any(), any())).thenReturn(locatorToBeClicked);
    when(page.waitForPopup(any())).thenAnswer(invocation -> {
      Runnable runnable = invocation.getArgument(0);
      runnable.run();
      return newPage;
    });
    
    
    PageAct pageAct = CawebUtils.navigateToNewTabUnderSidebarItemAndAct("Hello", "world", pageActForNewTab);
    pageAct.perform(page, env);
    
    verify(locator).click();
    verify(locatorToBeClicked).click();
    verify(pageActForNewTab).perform(newPage, env);
  }
}
