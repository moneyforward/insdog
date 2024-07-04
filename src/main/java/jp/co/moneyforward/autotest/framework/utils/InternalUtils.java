package jp.co.moneyforward.autotest.framework.utils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.exceptions.ActionException;
import com.github.valid8j.pcond.fluent.Statement;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.validator.Validator;
import org.opentest4j.TestAbortedException;
import org.opentest4j.TestSkippedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;
import static com.github.valid8j.pcond.internals.InternalUtils.getMethod;

public enum InternalUtils {
  ;
  public static class AssumptionViolation extends TestAbortedException {
    public AssumptionViolation(String message) {
      super(message);
    }
  }
  private static final Logger LOGGER = Logger.getLogger(InternalUtils.class.getName());
  
  public static Date date(String dateString) {
    try {
      return new SimpleDateFormat("MMM/dd/yyyy", Locale.US).parse(dateString);
    } catch (ParseException e) {
      throw wrap(e);
    }
  }
  
  public static Date today() {
    return new Date();
  }
  
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
        return consumerName.replaceAll("\n", " ");
      }
    };
  }
  
  public static Action action(String name, Consumer<Context> contextConsumer) {
    return leaf(printableConsumer(name, contextConsumer));
  }
  
  public static Predicate<Date> dateAfter(Date date) {
    return Printables.predicate("after[" + date + "]", d -> d.after(date));
  }
  
  /**
   * This is a method to workaround issue: link:https://github.com/valid8j/valid8j/issues/16[valid8j/issue-16]
   * Once it is fixed, usages of this method should be replaced with `Expectations.assumeStatement`.
   *
   * @param statement A statement
   * @param <T> Type of the value
   */
  @SuppressWarnings("JavadocLinkAsPlainText")
  public static <T> void assumeStatement(Statement<T> statement) {
    Validator.INSTANCE.get().validate(statement.statementValue(),
                                      statement.statementPredicate(),
                                      msg -> {
                                        throw new AssumptionViolation(msg);
                                      });
  }
  
  public static boolean isToStringOverridden(Object object) {
    return getMethod(object.getClass(), "toString").getDeclaringClass() != Object.class;
  }
  
  private static RuntimeException wrap(ParseException e) {
    throw new RuntimeException(e);
  }
}
