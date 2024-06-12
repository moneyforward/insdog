package jp.co.moneyforward.autotest.framework.facade;

import com.github.dakusui.actionunit.core.Context;

import java.util.function.Function;

public record Resolver(String parameterName, Function<Context, Object> resolverFunction) {
}
