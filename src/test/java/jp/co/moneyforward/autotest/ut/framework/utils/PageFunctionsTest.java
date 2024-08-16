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
  void givenNonLenient_whenGetByName_thenEqualSignContainedInName() {
    Function<Page, Locator> function = PageFunctions.getByName("test", false);
    
    assertStatement(value(function).stringify().toBe().containing("name=test"));
  }
  
  @Test
  void givenNonLenient_whenGetByName_thenLocatorIsReturned() {
    boolean v = false;
    Function<Page, Locator> function = PageFunctions.getByName("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByRole(any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void whenGetButtonByName_thenLocatorIsReturned() {
    boolean v = false;
    Function<Page, Locator> function = PageFunctions.getButtonByName("test");
    
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
    Function<Page, Locator> function = PageFunctions.getByLabel("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByLabel((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void givenNonLenient_whenGetByText_thenLocatorIsReturned() {
    boolean v = false;
    whenGetByLabel_thenLocatorIsReturned(v);
  }
  
  @Test
  void givenLenient_whenGetByText_thenLocatorIsReturned() {
    boolean v = true;
    whenGetByText_thenLocatorIsReturned(v);
  }
  
  private static void whenGetByText_thenLocatorIsReturned(boolean v) {
    Function<Page, Locator> function = PageFunctions.getByText("test", v);
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByText((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void givenNonLenient_whenGetByPlaceHolder_thenLocatorIsReturned() {
    Function<Page, Locator> function = PageFunctions.getByPlaceholder("test");
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.getByPlaceholder((String) any(), any())).thenReturn(mockedLocator);
      Locator locator = function.apply(page);
      
      assertStatement(value(locator).toBe().equalTo(mockedLocator));
    }
  }
  
  @Test
  void whenGetTitle_thenTitleIsReturned() {
    Function<Page, String> function = PageFunctions.getTitle();
    
    try (Page page = Mockito.mock(Page.class)) {
      Locator mockedLocator = mock(Locator.class);
      when(page.title()).thenReturn("helloTitle");
      String title = function.apply(page);
      
      assertStatement(value(title).toBe().equalTo("helloTitle"));
    }
  }
}
