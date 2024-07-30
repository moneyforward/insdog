package jp.co.moneyforward.autotest.ut.builtins;

import com.microsoft.playwright.Keyboard;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static com.github.valid8j.fluent.Expectations.*;
import static org.mockito.Mockito.*;

class BuiltInLeafActsTest extends TestBase {
  @Test
  void givenVisibleLocator_whenPerformClick_thenLocatorIsClicked() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      Locator locator = Mockito.mock(Locator.class);
      when(locator.isVisible()).thenReturn(true);
      doNothing().when(locator).click();
      
      Page returned = new Click(p -> locator).perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator).click();
    }
  }
  
  @Test
  void givenVisibleLocator_whenPerformClickIfPresent_thenLocatorIsClicked() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      Locator locator = Mockito.mock(Locator.class);
      when(locator.isVisible()).thenReturn(true);
      doNothing().when(locator).click();
      
      Page returned = new ClickIfPresent(p -> locator).perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator).click();
    }
  }
  
  @Test
  void givenInvisibleLocator_whenPerformClickIfPresent_thenLocatorIsClicked() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      Locator locator = Mockito.mock(Locator.class);
      when(locator.isVisible()).thenReturn(false);
      doNothing().when(locator).click();
      
      Page returned = new ClickIfPresent(p -> locator).perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator, never()).click();
    }
  }
  
  @Test
  void whenPerformScreenshot_thenScreenshotCalled() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      when(executionEnvironment.stepName()).thenReturn("TEST_STEP");
      when(executionEnvironment.testOutputFilenameFor(any())).thenCallRealMethod();
      when(executionEnvironment.testResultDirectory()).thenReturn(new File(".").toPath());
      when(page.screenshot(any())).thenReturn(new byte[]{0, 1});
      
      Page returned = new Screenshot().perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(page)
             .screenshot(argThat(screenshotOptions -> screenshotOptions.path.endsWith("screenshot-TEST_STEP.png")));
    }
  }
  
  @Test
  void givenClickIfPresent_whenName_thenNameLooksOk() {
    ClickIfPresent act = new ClickIfPresent(PageFunctions.getByText("hello"));
    
    String name = act.name();
    
    System.out.println(name);
    assertAll(value(name).toBe()
                         .containing("ClickIfPresent")
                         .containing("hello"));
  }
  
  @Test
  void givenUnmaskedString_whenSendkey_thenUnmaskedStringIsTypedIntoLocator() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      Locator locator = Mockito.mock(Locator.class);
      Keyboard keyboard = Mockito.mock(Keyboard.class);
      when(locator.isVisible()).thenReturn(true);
      doNothing().when(keyboard).type(any());
      when(page.locator(any())).thenReturn(locator);
      when(page.keyboard()).thenReturn(keyboard);
      
      Page returned = new SendKey("hello", "keysToBeSentToHello").perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator).focus();
      Mockito.verify(keyboard).type("keysToBeSentToHello");
    }
  }
  
  @Test
  void givenMaskedString_whenSendkey_thenUnmaskedStringIsTypedIntoLocator() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      Locator locator = Mockito.mock(Locator.class);
      Keyboard keyboard = Mockito.mock(Keyboard.class);
      when(locator.isVisible()).thenReturn(true);
      doNothing().when(keyboard).type(any());
      when(page.locator(any())).thenReturn(locator);
      when(page.keyboard()).thenReturn(keyboard);
      
      Page returned = new SendKey("hello", SendKey.MASK_PREFIX + "keysToBeSentToHello").perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator).focus();
      Mockito.verify(keyboard).type("keysToBeSentToHello");
    }
  }
  
  @Test
  void givenMaskedString_whenName_thenNameLooksOkWithoutUnmaskedString() {
    SendKey act = new SendKey("hello", SendKey.MASK_PREFIX + "keysToBeSentToHello");
    
    String name = act.name();
    
    System.out.println(name);
    assertStatement(value(name).toBe()
                               .containing("SendKey")
                               .not(v -> v.containing("keysToBeSentToHello"))
                               .containing(SendKey.MASK_PREFIX));
  }
  
  @Test
  void givenSendKey_whenName_thenNameLooksOk() {
    SendKey act = new SendKey("hello", "keysToBeSentToHello");
    
    String name = act.name();
    
    System.out.println(name);
    assertStatement(value(name).toBe()
                               .containing("SendKey")
                               .containing("keysToBeSentToHello"));
  }
  
}
