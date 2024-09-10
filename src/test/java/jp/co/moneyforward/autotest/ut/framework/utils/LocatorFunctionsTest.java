package jp.co.moneyforward.autotest.ut.framework.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.LocatorFunctions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class LocatorFunctionsTest {
  @Test
  void whenNth_thenLocatorNthIsCalled() {
    Locator locator = Mockito.mock(Locator.class);
    Locator returnedLocator = Mockito.mock(Locator.class);
    
    when(locator.nth(1)).thenReturn(returnedLocator);
    
    assertStatement(value(LocatorFunctions.nth(1).apply(locator))
                        .toBe()
                        .equalTo(returnedLocator));
  }
  
  @Test
  void whenByText_thenLocatorGetByTextIsCalled() {
    Locator locator = Mockito.mock(Locator.class);
    Locator returnedLocator = Mockito.mock(Locator.class);
    
    when(locator.getByText("hello")).thenReturn(returnedLocator);
    
    assertStatement(value(LocatorFunctions.byText("hello").apply(locator))
                        .toBe()
                        .equalTo(returnedLocator));
  }
  
  @Test
  void whenByNameExact_thenLocatorGetByTextIsCalled() {
    Locator locator = Mockito.mock(Locator.class);
    Locator returnedLocator = Mockito.mock(Locator.class);
    when(locator.getByRole(eq(AriaRole.LINK), any())).thenReturn(returnedLocator);
    
    assertStatement(value(LocatorFunctions.byName("name", true).apply(locator))
                        .toBe()
                        .equalTo(returnedLocator));
  }
  
  @Test
  void whenTextContent_thenLocatorTextContentIsCalled() {
    Locator locator = Mockito.mock(Locator.class);
    
    when(locator.textContent()).thenReturn("Hello!");
    
    assertStatement(value(LocatorFunctions.textContent().apply(locator))
                        .toBe()
                        .equalTo("Hello!"));
  }
}
