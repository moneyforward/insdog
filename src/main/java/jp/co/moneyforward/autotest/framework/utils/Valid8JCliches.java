package jp.co.moneyforward.autotest.framework.utils;

import com.github.valid8j.pcond.core.printable.PrintablePredicateFactory;
import com.github.valid8j.pcond.fluent.Statement;
import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.validator.Validator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

///
/// A container class for utility classes for **valid8j** usages.
///
/// This class may hold utility methods and various inner classes.
/// They should be fed back to the **valid8j** community and once those are implemented as its out-of-box features,
/// usages of them should be replaced with the new features in **valid8j**.
///
///
public enum Valid8JCliches {
  ;
  
  ///
  /// This is a method to workaround issue: [valid8j:issue-16](https://github.com/valid8j/valid8j/issues/16[valid8j/issue-16)
  /// Once it is fixed, usages of this method should be replaced with `Expectations.assumeStatement`.
  ///
  /// @param statement A statement
  /// @param <T>       Type of the value
  ///
  @SuppressWarnings("JavadocLinkAsPlainText")
  public static <T> void assumeStatement(Statement<T> statement) {
    Validator.INSTANCE.get().validate(statement.statementValue(),
                                      statement.statementPredicate(),
                                      msg -> {
                                        throw new InternalUtils.AssumptionViolation(msg);
                                      });
  }
  
  public static <K, V> Function<Map<K, V>, List<K>> mapToKeyList() {
    return Printables.function("mapToKeyList", m -> m.keySet().stream().toList());
  }
  
  ///
  /// A utility class to make an existing `Function` s  and `Predicate` s "printable".
  ///
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
  
  public enum Transform {
    ;
    
    public static <T, R> PrintablePredicateFactory.TransformingPredicate.Factory<R, T> $(Function<T, R> function) {
      return Predicates.transform(function);
    }
  }
}
