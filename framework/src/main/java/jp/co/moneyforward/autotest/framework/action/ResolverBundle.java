package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A bundle of variable resolver.
 *
 * A variable resolver figures out a value of a variable specified by a variable name.
 * This class bundles a set of such resolvers and associated each resolver with a variable name which the resolver
 * should figure out the value.
 */
public class ResolverBundle extends HashMap<String, Function<Context, Object>> {
  /**
   * Creates an instance of this class.
   *
   * @param resolvers A map of resolvers from variable names to resolvers.
   */
  public ResolverBundle(Map<String, Function<Context, Object>> resolvers) {
    super(resolvers);
  }
  
  /**
   * Creates an instance of this class.
   *
   * @param resolvers A list of resolvers from which an object of this class will be created.
   */
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
  
  public static ResolverBundle emptyResolverBundle() {
    return new ResolverBundle(List.of());
  }
}
