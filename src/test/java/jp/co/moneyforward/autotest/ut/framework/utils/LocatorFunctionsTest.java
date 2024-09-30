package jp.co.moneyforward.autotest.ut.framework.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.LocatorFunctions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;
import static org.mockito.ArgumentMatchers.*;
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
    boolean lenient = false; // exact = !lenient = false
    when(locator.getByRole(eq(AriaRole.LINK), argThat(roleOptions -> roleOptions.exact == !lenient))).thenReturn(returnedLocator);
    
    assertStatement(value(LocatorFunctions.byName("name", lenient).apply(locator))
                        .toBe()
                        .equalTo(returnedLocator));
  }
  
  @Test
  void whenByNameLenient_thenLocatorGetByTextIsCalled() {
    Locator locator = Mockito.mock(Locator.class);
    Locator returnedLocator = Mockito.mock(Locator.class);
    boolean lenient = true;
    when(locator.getByRole(eq(AriaRole.LINK), argThat(roleOptions -> roleOptions.exact == !lenient))).thenReturn(returnedLocator);
    
    assertStatement(value(LocatorFunctions.byName("name", lenient).apply(locator))
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
