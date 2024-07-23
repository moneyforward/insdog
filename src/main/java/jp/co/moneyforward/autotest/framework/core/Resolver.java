package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.actionunit.core.Context;

import java.util.Map;
import java.util.function.Function;

public record Resolver(String parameterName, Function<Context, Object> resolverFunction) {
  public static Function<Context, Object> valueFrom(String sourceSceneName, String fieldNameInSourceScene) {
    return context -> context.<Map<String, Object>>valueOf(sourceSceneName).get(fieldNameInSourceScene);
  }
  
  public static Resolver resolverFor(String sceneName, String variableName) {
    return new Resolver(variableName, valueFrom(sceneName, variableName));
  }
}
