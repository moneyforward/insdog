package jp.co.moneyforward.autotest.framework.core;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Predicates.isInstanceOf;
import static com.github.valid8j.pcond.forms.Predicates.not;

public record Atom(Object value) implements Element {
  public static final Atom NULL_VALUE = new Atom(null);
  
  public Atom(Object value) {
    assert precondition(that(value).satisfies()
                                   .predicate(not(isInstanceOf(Element.class))));
    this.value = requireArgument(that(value).satisfies()
                                            .anyOf()
                                            .instanceOf(String.class)
                                            .instanceOf(Number.class)
                                            .instanceOf(Object.class)
                                            .nullValue());
  }
}
