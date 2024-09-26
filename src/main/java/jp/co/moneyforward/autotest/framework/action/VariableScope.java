package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

/**
 * Can a name of a scene call be a name of variable?
 * If so, what does it mean?
 */
public interface VariableScope {
  <T> T resolve(String name);
  
  boolean isDefined(String name);
  
  /**
   * Creates a new scope instance for a given `context`.
   *
   * @param context             An **actionunit** context, in which a variable scope object is created.
   * @param inputVariableScopes Names of variableScopes from which variables are imported.
   * @return A new variable scope
   */
  static VariableScope create(Context context, String... inputVariableScopes) {
    return null;
  }
  
  static VariableScope createRootScope(Context context) {
    requireNonNull(context);
    return new VariableScope() {
      @Override
      public <T> T resolve(String name) {
        return context.valueOf(name);
      }
      
      @Override
      public boolean isDefined(String name) {
        return context.defined(name);
      }
    };
  }
}
