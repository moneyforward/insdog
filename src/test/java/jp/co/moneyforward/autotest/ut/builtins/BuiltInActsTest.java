package jp.co.moneyforward.autotest.ut.builtins;

import com.github.valid8j.pcond.forms.Printables;
import com.microsoft.playwright.*;
import io.appium.java_client.AppiumDriver;
import jp.co.moneyforward.autotest.actions.mobile.ElementFunctions;
import jp.co.moneyforward.autotest.actions.web.*;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.valid8j.fluent.Expectations.*;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.MASK_PREFIX;
import static org.mockito.Mockito.*;

class BuiltInActsTest extends TestBase {
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
    ClickIfPresent act = new ClickIfPresent(PageFunctions.locatorByText("hello"));
    
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
      
      Page returned = new SendKey("hello", MASK_PREFIX + "keysToBeSentToHello").perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(locator).focus();
      Mockito.verify(keyboard).type("keysToBeSentToHello");
    }
  }
  
  @Test
  void givenMaskedString_whenName_thenNameLooksOkWithoutUnmaskedString() {
    SendKey act = new SendKey("hello", MASK_PREFIX + "keysToBeSentToHello");
    
    String name = act.name();
    
    System.out.println(name);
    assertStatement(value(name).toBe()
                               .containing("SendKey")
                               .not(v -> v.containing("keysToBeSentToHello"))
                               .containing(MASK_PREFIX));
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
  
  @Test
  void givenNonNullUrl_whenNavigatePerformed_thenNavigateCalled() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      when(page.navigate(any())).thenReturn(mock(Response.class));
      
      String givenUrl = "http://www.example.com/hello/world";
      Page returned = new Navigate(givenUrl).perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(page).navigate(argThat(v -> v.equals(givenUrl)));
    }
  }
  
  @Test
  void whenNavigateName_thenNameLooksOk() {
    String givenUrl = "TestNavigateDescription";
    
    String name = new Navigate(givenUrl).name();
    
    assertAll(value(name).toBe()
                         .containing("Navigate")
                         .containing(givenUrl));
  }

  @Test
  void whenPageActPerformed_then() {
    try (Page page = Mockito.mock(Page.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      when(page.navigate(any())).thenReturn(mock(Response.class));
      
      String givenDescription = "TestPageActDescription";
      String givenUrl = "http://www.example.com/hello/world";
      Page returned = new PageAct(givenDescription) {
        @Override
        protected void action(Page page, ExecutionEnvironment executionEnvironment) {
          page.navigate(givenUrl);
        }
      }.perform(page, executionEnvironment);
      
      assertAll(value(returned).toBe().equalTo(page));
      Mockito.verify(page).navigate(argThat(v -> v.equals(givenUrl)));
    }
  }
  
  @Test
  void whenPageActName_thenNameLooksOk() {
    String givenDescription = "TestPageActDescription";
    
    String name = new PageAct(givenDescription) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
      }
    }.name();
    
    assertAll(value(name).toBe()
                         .containing("PageAct")
                         .containing(givenDescription));
  }
  
  @Test
  void whenCloseBrowserPerformed_thenCloseCalled() {
    try (Browser browser = Mockito.mock(Browser.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      doNothing().when(browser).close();
      
      Void returned = new CloseBrowser().perform(browser, executionEnvironment);
      
      assertAll(value(returned).toBe().nullValue());
      Mockito.verify(browser).close();
    }
  }
  
  @Test
  void whenCloseWindowPerformed_thenCloseCalled() {
    try (Playwright window = Mockito.mock(Playwright.class)) {
      ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
      doNothing().when(window).close();
      
      Void returned = new CloseWindow().perform(window, executionEnvironment);
      
      assertAll(value(returned).toBe().nullValue());
      Mockito.verify(window).close();
    }
  }
  
  @Test
  void givenFuncWithPrintableFunction_whenName_thenValueFromOverriddenToStringIsReturned() {
    Act.Func<String, String> func = new Act.Func<>(Printables.function("printableHello", x -> x));
    
    assertStatement(value(func.name()).toBe().equalTo("printableHello"));
  }
  
  @Test
  void givenFuncWithNonPrintableFunction_whenName_thenFixedValueIsReturned() {
    Act.Func<String, String> func = new Act.Func<>(x -> x);
    
    assertStatement(value(func.name()).toBe().equalTo("func"));
  }
  
  @Test
  void givenSinkWithName_whenName_thenValueFromOverriddenToStringIsReturned() {
    Act.Sink<String> func = new Act.Sink<>("printableHello", x -> {});
    
    assertStatement(value(func.name()).toBe().equalTo("printableHello"));
  }
  
  @Test
  void givenSinkWithoutName_whenName_thenFixedValueIsReturned() {
    Act.Sink<String> sink = new Act.Sink<>(x -> {});
    
    assertStatement(value(sink.name()).toBe().equalTo("sink"));
  }
  
  @Test
  void givenSinkWithoutName_whenPerformed_thenGivenConsumerExercised() {
    var valueHolder = new AtomicReference<String>();
    Act.Sink<String> sink = new Act.Sink<>(valueHolder::set);
    
    sink.perform("XYZ", mock(ExecutionEnvironment.class));
    
    assertStatement(value(valueHolder).invoke("get").toBe().equalTo("XYZ"));
  }

  // Mobile action tests

  @Test
  void givenMobileElement_whenPerformMobileClick_thenElementIsClicked() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    WebElement element = Mockito.mock(WebElement.class);
    By by = By.id("someId");
    when(driver.findElement(by)).thenReturn(element);

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.Click(by).perform(driver, executionEnvironment);

    assertAll(value(returned).toBe().equalTo(driver));
    Mockito.verify(element).click();
  }

  @Test
  void givenMobileClick_whenName_thenNameContainsClick() {
    jp.co.moneyforward.autotest.actions.mobile.Click act =
        new jp.co.moneyforward.autotest.actions.mobile.Click(By.id("someId"));

    String name = act.name();

    assertAll(value(name).toBe().containing("Click"));
  }

  @Test
  void givenPresentMobileElement_whenPerformMobileClickIfPresent_thenElementIsClicked() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    WebElement element = Mockito.mock(WebElement.class);
    By by = By.id("someId");
    when(driver.findElements(by)).thenReturn(List.of(element));
    when(element.isDisplayed()).thenReturn(true);

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.ClickIfPresent(by).perform(driver, executionEnvironment);

    assertAll(value(returned).toBe().equalTo(driver));
    Mockito.verify(element).click();
  }

  @Test
  void givenAbsentMobileElement_whenPerformMobileClickIfPresent_thenNoClickPerformed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    By by = By.id("someId");
    when(driver.findElements(by)).thenReturn(List.of());

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.ClickIfPresent(by).perform(driver, executionEnvironment);

    assertAll(value(returned).toBe().equalTo(driver));
    Mockito.verify(driver, never()).findElement(any());
  }

  @Test
  void givenMobileClickIfPresent_whenName_thenNameContainsClickIfPresent() {
    jp.co.moneyforward.autotest.actions.mobile.ClickIfPresent act =
        new jp.co.moneyforward.autotest.actions.mobile.ClickIfPresent(By.id("someId"));

    String name = act.name();

    assertAll(value(name).toBe().containing("ClickIfPresent"));
  }

  @Test
  void whenElementFunctionsTextContent_thenGetTextCalledOnElement() {
    WebElement element = Mockito.mock(WebElement.class);
    when(element.getText()).thenReturn("Hello");

    String text = ElementFunctions.textContent().apply(element);

    assertAll(value(text).toBe().equalTo("Hello"));
    Mockito.verify(element).getText();
  }

  @Test
  void whenElementFunctionsTagContent_thenGetTagNameCalledOnElement() {
    WebElement element = Mockito.mock(WebElement.class);
    when(element.getTagName()).thenReturn("button");

    String tag = ElementFunctions.tagContent().apply(element);

    assertAll(value(tag).toBe().equalTo("button"));
    Mockito.verify(element).getTagName();
  }

  @Test
  void whenElementFunctionsIsEnabled_thenIsEnabledCalledOnElement() {
    WebElement element = Mockito.mock(WebElement.class);
    when(element.isEnabled()).thenReturn(true);

    Boolean enabled = ElementFunctions.isEnabled().apply(element);

    assertAll(value(enabled).toBe().equalTo(true));
    Mockito.verify(element).isEnabled();
  }

  @Test
  void whenPageFunctionsLinkLocatorByName_thenExactMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.linkLocatorByName("hello").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("@content-desc='hello'")
                                                        .containing("@name='hello'"));
  }

  @Test
  void whenPageFunctionsLinkLocatorByNameLenient_thenContainsMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.linkLocatorByName("hello", true).apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("contains(@content-desc,'hello')")
                                                        .containing("contains(@name,'hello')"));
  }

  @Test
  void whenPageFunctionsLocatorByText_thenExactMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorByText("hello").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("@text='hello'")
                                                        .containing("@label='hello'")
                                                        .containing("@name='hello'"));
  }

  @Test
  void whenPageFunctionsLocatorByTextLenient_thenContainsMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorByText("hello", true).apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("contains(@text,'hello')")
                                                        .containing("contains(@label,'hello')")
                                                        .containing("contains(@name,'hello')"));
  }

  @Test
  void whenPageFunctionsButtonLocatorByName_thenCorrectXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.buttonLocatorByName("Submit").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("android.widget.Button[@text='Submit']")
                                                        .containing("XCUIElementTypeButton[@name='Submit']"));
  }

  @Test
  void whenPageFunctionsLocatorByLabel_thenExactMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorByLabel("myLabel").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("@content-desc='myLabel'")
                                                        .containing("@label='myLabel'"));
  }

  @Test
  void whenPageFunctionsLocatorByLabelLenient_thenContainsMatchXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorByLabel("myLabel", true).apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("contains(@content-desc,'myLabel')")
                                                        .containing("contains(@label,'myLabel')"));
  }

  @Test
  void whenPageFunctionsLocatorByPlaceholder_thenCorrectXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorByPlaceholder("Enter name").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("@hint='Enter name'")
                                                        .containing("@placeholderValue='Enter name'"));
  }

  @Test
  void whenPageFunctionsLocatorBySelector_thenFindElementCalledWithGivenBy() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    By by = By.id("targetId");
    when(driver.findElement(by)).thenReturn(element);

    WebElement result = jp.co.moneyforward.autotest.actions.mobile.PageFunctions.locatorBySelector(by).apply(driver);

    assertAll(value(result).toBe().equalTo(element));
    Mockito.verify(driver).findElement(by);
  }

  @Test
  void whenPageFunctionsLinkLocatorByText_thenLenientXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.linkLocatorByText("hello").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("contains(@text,'hello')")
                                                        .containing("contains(@label,'hello')")
                                                        .containing("contains(@name,'hello')"));
  }

  @Test
  void whenPageFunctionsLinkLocatorByExactText_thenExactXpathUsed() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    WebElement element = Mockito.mock(WebElement.class);
    ArgumentCaptor<By> byCaptor = ArgumentCaptor.forClass(By.class);
    when(driver.findElement(any(By.class))).thenReturn(element);

    jp.co.moneyforward.autotest.actions.mobile.PageFunctions.linkLocatorByExactText("hello").apply(driver);

    Mockito.verify(driver).findElement(byCaptor.capture());
    assertStatement(value(byCaptor.getValue().toString()).toBe()
                                                        .containing("@text='hello'")
                                                        .containing("@label='hello'")
                                                        .containing("@name='hello'"));
  }

  @Test
  void whenPageFunctionsToTitle_thenDriverGetTitleIsCalled() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    when(driver.getTitle()).thenReturn("My Title");

    String title = jp.co.moneyforward.autotest.actions.mobile.PageFunctions.toTitle().apply(driver);

    assertAll(value(title).toBe().equalTo("My Title"));
    Mockito.verify(driver).getTitle();
  }

  @Test
  void whenPerformMobileScreenshot_thenScreenshotCopiedAndDriverReturned() throws IOException {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    when(executionEnvironment.stepName()).thenReturn("TEST_STEP");
    Path destPath = Path.of(System.getProperty("java.io.tmpdir"), "screenshot-test-" + System.nanoTime(), "screenshot-dest.png");
    when(driver.getScreenshotAs(any())).thenReturn(new byte[]{1, 2, 3});
    when(executionEnvironment.testOutputFilenameFor(any(String.class))).thenReturn(destPath);

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.Screenshot().perform(driver, executionEnvironment);

    assertAll(
        value(returned).toBe().equalTo(driver),
        value(destPath.toFile().exists()).toBe().equalTo(true));
    Mockito.verify(driver).getScreenshotAs(any());
  }

  @Test
  void givenUnmaskedKey_whenPerformMobileSendKey_thenKeysSentToElement() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    WebElement element = Mockito.mock(WebElement.class);
    By by = By.id("inputField");
    when(driver.findElement(by)).thenReturn(element);

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.SendKey(by, "myPassword").perform(driver, executionEnvironment);

    assertAll(value(returned).toBe().equalTo(driver));
    Mockito.verify(element).sendKeys("myPassword");
  }

  @Test
  void givenMaskedKey_whenPerformMobileSendKey_thenUnmaskedKeysSentToElement() {
    AppiumDriver driver = Mockito.mock(AppiumDriver.class);
    ExecutionEnvironment executionEnvironment = Mockito.mock(ExecutionEnvironment.class);
    WebElement element = Mockito.mock(WebElement.class);
    By by = By.id("inputField");
    when(driver.findElement(by)).thenReturn(element);

    AppiumDriver returned = new jp.co.moneyforward.autotest.actions.mobile.SendKey(
        by, MASK_PREFIX + "myPassword"
    ).perform(driver, executionEnvironment);

    assertAll(value(returned).toBe().equalTo(driver));
    Mockito.verify(element).sendKeys("myPassword");
  }

  @Test
  void givenMobileSendKey_whenName_thenNameContainsSendKeyAndLocatorAndKeys() {
    jp.co.moneyforward.autotest.actions.mobile.SendKey act =
        new jp.co.moneyforward.autotest.actions.mobile.SendKey(By.id("field"), "keys");

    String name = act.name();

    assertStatement(value(name).toBe()
                               .containing("SendKey")
                               .containing("field")
                               .containing("keys"));
  }

  @Test
  void givenMobileSendKeyWithMaskedKey_whenName_thenNameContainsMaskPrefixNotSecret() {
    jp.co.moneyforward.autotest.actions.mobile.SendKey act =
        new jp.co.moneyforward.autotest.actions.mobile.SendKey(By.id("field"), MASK_PREFIX + "secret");

    String name = act.name();

    assertStatement(value(name).toBe()
                               .containing("SendKey")
                               .containing(MASK_PREFIX)
                               .not(v -> v.containing("secret")));
  }
}
