package jp.co.moneyforward.autotest.ut.misc;

import static com.github.valid8j.fluent.Expectations.requireArgument;
import static com.github.valid8j.fluent.Expectations.that;

public record Entry(String key, Element value) {
  public Entry(String key, Element value) {
    this.key = requireArgument(that(key).satisfies().notNull());
    this.value = requireArgument(that(value).satisfies().notNull());
  }
}
