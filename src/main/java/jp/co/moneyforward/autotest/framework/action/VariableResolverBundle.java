package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface VariableResolverBundle extends Map<String, Function<Context, Object>> {
  static VariableResolverBundle create(Map<String, Function<Context, Object>> map) {
    class Impl extends HashMap<String, Function<Context, Object>> implements VariableResolverBundle {
      public Impl(Map<String, Function<Context, Object>> map) {
        super(map);
      }
    }
    return new Impl(map);
  }
}
