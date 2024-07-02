package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

public record Resolver(String parameterName, Function<Context, Object> resolverFunction) {
  @SuppressWarnings("unchecked")
  public static Resolver parameterFromScene(String parameterName, String sourceSceneName, String fieldNameInSourceScene) {
    return new Resolver(parameterName, c -> ((Map<String, Object>) c.valueOf(sourceSceneName)).get(fieldNameInSourceScene));
  }
}
