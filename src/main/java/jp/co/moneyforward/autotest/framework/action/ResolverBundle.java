package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;
import jp.co.moneyforward.autotest.framework.core.Resolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ResolverBundle extends HashMap<String, Function<Context, Object>> {
  public ResolverBundle(Map<String, Function<Context, Object>> resolvers) {
    super(resolvers);
  }
  
  public ResolverBundle(List<Resolver> resolvers) {
    this(resolverToMap(resolvers));
  }
  
  private static Map<String, Function<Context, Object>> resolverToMap(List<Resolver> resolvers) {
    Map<String, Function<Context, Object>> resolverMap = new HashMap<>();
    for (Resolver resolver : resolvers) {
      resolverMap.put(resolver.variableName(), resolver.resolverFunction());
    }
    return resolverMap;
  }
}
