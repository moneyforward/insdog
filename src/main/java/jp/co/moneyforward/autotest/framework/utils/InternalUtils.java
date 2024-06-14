package jp.co.moneyforward.autotest.framework.utils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.exceptions.ActionException;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;
import static java.util.stream.Stream.concat;

public enum InternalUtils {
  ;
  
  @SafeVarargs
  public static <T> Stream<T> concat(Stream<T>... streams) {
    if (streams.length == 0)
      return Stream.empty();
    if (streams.length == 1)
      return streams[0];
    if (streams.length == 2)
      return Stream.concat(streams[0], streams[1]);
    else
      return Stream.concat(streams[0], concat(Arrays.copyOfRange(streams, 1, streams.length)));
  }
  
  public static Consumer<Context> printableConsumer(final String consumerName, Consumer<Context> consumer) {
    return new Consumer<>() {
      @Override
      public void accept(Context context) {
        consumer.accept(context);
      }
      
      @Override
      public String toString() {
        return consumerName;
      }
    };
  }
  
  public static Action action(String name, Consumer<Context> contextConsumer) {
    return leaf(printableConsumer(name, contextConsumer));
  }
  
  public static Action rethrow() {
    return action("rethrow", context -> {
      try {
        throw context.thrownException();
      } catch (Error | RuntimeException e) {
        throw e;
      } catch (Throwable e) {
        throw new ActionException(e);
      }
    });
  }
}
