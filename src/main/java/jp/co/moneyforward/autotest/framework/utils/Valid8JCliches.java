package jp.co.moneyforward.autotest.framework.utils;

import com.github.valid8j.pcond.forms.Printables;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Valid8JCliches {
  ;
  
  public enum MakePrintable {
    ;
    
    public static <T, R> FunctionConvertibleToPrintable<T, R> function(Function<T, R> f) {
      return f::apply;
    }
    
    public static <T> PredicateConvertibleToPrintable<T> predicate(Predicate<T> p) {
      return p::test;
    }
    
    public interface FunctionConvertibleToPrintable<T, R> extends Function<T, R> {
      default Function<T, R> $(String fmt, Object... args) {
        return Printables.function(String.format(fmt, args), this);
      }
    }
    
    public interface PredicateConvertibleToPrintable<T> extends Predicate<T> {
      default Predicate<T> $(String fmt, Object... args) {
        return Printables.predicate(String.format(fmt, args), this);
      }
    }
  }
}
