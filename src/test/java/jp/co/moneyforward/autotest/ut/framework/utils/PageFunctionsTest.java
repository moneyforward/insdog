package jp.co.moneyforward.autotest.ut.framework.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageFunctions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PageFunctionsTest {
  @Test
  void givenNonLenient_whenLinkLocatorByName_thenEqualSignContainedInName() {
    Function<Page, Locator> function = PageFunctions.linkLocatorByName("test", false);
    
    assertStatement(value(function).stringify().toBe().containing("name=test"));
  }
  
  @Test
  void givenNonLenient_whenLocatorByName_thenLinkLocatorIsReturned() {
    boolean v = false;
    Function<Page, Locator> function = PageFunctions.linkLocatorByName("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByRole(any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void whenButtonLocatorByName_thenLocatorIsReturned() {
    boolean v = false;
    Function<Page, Locator> function = PageFunctions.buttonLocatorByName("test");
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByRole(any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  
  @Test
  void givenNonLenient_whenGetByLabel_thenLocatorIsReturned() {
    boolean v = false;
    whenGetByLabel_thenLocatorIsReturned(v);
  }
  
  @Test
  void givenLenient_whenGetByLabel_thenLocatorIsReturned() {
    boolean v = true;
    whenGetByLabel_thenLocatorIsReturned(v);
  }
  
  private static void whenGetByLabel_thenLocatorIsReturned(boolean v) {
    Function<Page, Locator> function = PageFunctions.locatorByLabel("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByLabel((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void givenNonLenient_whenLocatorByText_thenLocatorIsReturned() {
    boolean v = false;
    whenLocatorByText_thenLocatorIsReturned(v);
  }
  
  @Test
  void givenLenient_whenLocatorByText_thenLocatorIsReturned() {
    boolean v = true;
    whenLocatorByText_thenLocatorIsReturned(v);
  }
  
  private static void whenLocatorByText_thenLocatorIsReturned(boolean v) {
    Function<Page, Locator> function = PageFunctions.locatorByText("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByText((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void givenNonLenient_whenLinkLocatorByText_thenLocatorIsReturned() {
    boolean v = false;
    whenLinkLocatorByText_thenLinkLocatorIsReturned(v);
  }
  
  @Test
  void whenLinkLocatorByText_thenLocatorIsReturned() {
    boolean v = false;
    thenLinkLocatorIsReturned(PageFunctions.linkLocatorByText("test"));
  }
  
  @Test
  void givenLenient_whenLinkLocatorByText_thenLocatorIsReturned() {
    boolean v = true;
    whenLinkLocatorByText_thenLinkLocatorIsReturned(v);
  }
  
  @Test
  void whenLinkLocatorByExactText_thenLocatorIsReturned() {
    boolean v = false;
    thenLinkLocatorIsReturned(PageFunctions.linkLocatorByExactText("test"));
  }
  
  private static void whenLinkLocatorByText_thenLinkLocatorIsReturned(boolean v) {
    thenLinkLocatorIsReturned(PageFunctions.linkLocatorByText("test", v));
  }
  
  private static void thenLinkLocatorIsReturned(Function<Page, Locator> function) {
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByRole(any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void givenNonLenient_whenLocatorByPlaceHolder_thenLocatorIsReturned() {
    Function<Page, Locator> function = PageFunctions.locatorByPlaceholder("test");
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByPlaceholder((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void whenLocatorForTitle_thenTitleIsReturned() {
    Function<Page, String> function = PageFunctions.toTitle();
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.title()).thenReturn("helloTitle");
      String title = function.apply(page);
      
      assertStatement(value(title).toBe().equalTo("helloTitle"));
    }
  }
}
