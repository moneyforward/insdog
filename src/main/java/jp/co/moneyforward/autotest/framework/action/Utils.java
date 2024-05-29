package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.exceptions.ActionException;

import java.util.function.Consumer;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;

public enum Utils {
  ;
  
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
  
  static Action action(String name, Consumer<Context> contextConsumer) {
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
