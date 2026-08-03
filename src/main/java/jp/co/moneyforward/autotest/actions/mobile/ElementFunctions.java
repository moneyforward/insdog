package jp.co.moneyforward.autotest.actions.mobile;

import com.github.valid8j.pcond.forms.Printables;
import org.openqa.selenium.WebElement;

import java.util.function.Function;

public enum ElementFunctions {;
    private ElementFunctions() {
    }
  
    public static Function<WebElement, String> textContent() {
      return Printables.function("textContent", WebElement::getText);
    }
    
    public static Function<WebElement, String> tagContent() {
      return Printables.function("tagContent", WebElement::getTagName);
    }
    
    public static Function<WebElement, Boolean> isEnabled() {
      return Printables.function("isEnabled", WebElement::isEnabled);
    }
    
    public static Function<WebElement, Boolean> isDisplayed() {
      return Printables.function("isDisplayed", WebElement::isDisplayed);
    }
}
