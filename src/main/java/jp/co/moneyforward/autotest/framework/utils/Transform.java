package jp.co.moneyforward.autotest.framework.utils;

import com.github.valid8j.pcond.core.printable.PrintablePredicateFactory;
import com.github.valid8j.pcond.forms.Predicates;

import java.util.function.Function;

public enum Transform {
  ;
  
  public static <T, R> PrintablePredicateFactory.TransformingPredicate.Factory<R, T> $(Function<T, R> function) {
    return Predicates.transform(function);
  }
}
