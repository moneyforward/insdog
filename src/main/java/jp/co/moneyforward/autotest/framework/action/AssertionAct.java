package jp.co.moneyforward.autotest.framework.action;

import com.github.valid8j.pcond.fluent.Statement;

import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.util.Collections.singletonList;

/**
 * **AssertionAct** is an **Act** which checks an output of a **LeafFact** held by an instance of this class as a "parent".
 *
 * @param <T> An input type of parent **LeafAct**.
 * @param <R> An output type of parent **LeafAct**.
 *
 * @see Act
 * @see LeafAct
 */
public class AssertionAct<T, R> implements Act {
  private final List<Function<R, Statement<R>>> assertions;
  private final String name;
  private final LeafAct<T, R> parent;
  
  /**
   * Creates a new  instance of this class.
   *
   * @param parent A parent `LeafAct` object.
   * @param name A name of this object.
   *
   * @param assertion An assertion that verifies an output of `parent`.
   */
  public AssertionAct(LeafAct<T, R> parent, String name, Function<R, Statement<R>> assertion) {
    this(parent, name, singletonList(assertion));
  }
  
  /**
   * Creates a new instance of this class.
   *
   * @param parent A parent `LeafAct` object.
   * @param name A name of this object.
   * @param assertions Assertions that verify an output of `parent`.
   */
  public AssertionAct(LeafAct<T, R> parent, String name, List<Function<R, Statement<R>>> assertions) {
    this.parent = parent;
    this.assertions = requireNonNull(assertions);
    this.name = requireNonNull(name);
  }
  
  /**
   * Returns a parent `LeafAct` instance of this object.
   *
   * @return A parent `LeafAct` instance of this object.
   */
  public LeafAct<T, R> parent() {
    return this.parent;
  }
  
  /**
   * A list of assertions that verifies an output of a parent `LeafAct`.
   *
   * @return assertions for the output of a parent `LeafAct`.
   */
  public List<Function<R, Statement<R>>> assertions() {
    return this.assertions;
  }
  
  @Override
  public String name() {
    return this.name;
  }
}
