package jp.co.moneyforward.autotest.framework.utils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.fluent.Statement;
import com.github.valid8j.pcond.forms.Printables;
import com.github.valid8j.pcond.validator.Validator;
import org.opentest4j.TestAbortedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.actionunit.core.ActionSupport.leaf;
import static com.github.valid8j.pcond.internals.InternalUtils.getMethod;

/**
 * An internal utility class of the **autotest-ca** framework.
 */
public enum InternalUtils {
  ;
  
  public static boolean isPresumablyRunningOnLaptop() {
    return !isRunUnderPitest()
        && !isRunUnderSurefire();
  }
  
  
  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }
  
  public static boolean isRunUnderPitest() {
    return Objects.equals(System.getProperty("underpitest"), "yes");
  }
  
  public static String composeResultMessageLine(String line, String stageName) {
    return String.format("%-11s %s", stageName + ":", line);
  }
  
  public static class AssumptionViolation extends TestAbortedException {
    public AssumptionViolation(String message) {
      super(message);
    }
  }
  
  /**
   * Creates a `Date` object from a string formatted with `MMM/dd/yyyy`.
   * `Locale.US` is used to create a `SimpleDateFormat` object.
   *
   * @param dateString A string from which a `Date` object is created.
   * @return A date object created from `dateString`.
   */
  public static Date date(String dateString) {
    try {
      return new SimpleDateFormat("MMM/dd/yyyy", Locale.US).parse(dateString);
    } catch (ParseException e) {
      throw wrap(e);
    }
  }
  
  /**
   * Returns a `Date` object from the current date.
   *
   * @return A date object created from the current date.
   */
  public static Date today() {
    return new Date();
  }
  
  /**
   * Concatenates given streams.
   *
   * @param streams Streams to be concatenated.
   * @param <T>     The type of the values streamed by the given `streams`.
   * @return Concatenated stream.
   */
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
  
  /**
   * Creates a consumer, which gives a `consumerName`, when `toString` method is called.
   *
   * @param consumerName A name of the created consumer. Returned from `toString`.
   * @param consumer     A consumer from which the returned object is created.
   * @return A consumer which executes the `accept` method of the consumer and returns `consumerName` for `toString`.
   */
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
  
  /**
   * Creates a leaf action, which executes the `accept` method of `contextConsumer`.
   * Inside this method, the given `contextConsumer` method is made printable using the `printableConsumer` method.
   * Then it will be passed to `ActionSupport#leaf` method to turn it into an action.
   *
   * @param name            A name of the action.
   * @param contextConsumer A consumer to define the behavior of the returned action.
   * @return A leaf action created from the `contextConsumer`.
   */
  public static Action action(String name, Consumer<Context> contextConsumer) {
    return leaf(printableConsumer(name, contextConsumer));
  }
  
  /**
   * Returns a predicate that tests if the date given to it is after the `date`.
   *
   * @param date The returned predicate returns `true` if a given date is after this.
   * @return A predicate to check if a given date is after `date`.
   */
  public static Predicate<Date> dateAfter(Date date) {
    return Printables.predicate("after[" + date + "]", d -> d.after(date));
  }
  
  /**
   * This is a method to workaround issue: [valid8j:issue-16](https://github.com/valid8j/valid8j/issues/16[valid8j/issue-16)
   * Once it is fixed, usages of this method should be replaced with `Expectations.assumeStatement`.
   *
   * @param statement A statement
   * @param <T>       Type of the value
   */
  @SuppressWarnings("JavadocLinkAsPlainText")
  public static <T> void assumeStatement(Statement<T> statement) {
    Validator.INSTANCE.get().validate(statement.statementValue(),
                                      statement.statementPredicate(),
                                      msg -> {
                                        throw new AssumptionViolation(msg);
                                      });
  }
  
  /**
   * Checks if the given `object` has a `toString` method which overrides `Object#toString`.
   *
   * @param object An object to be checked.
   * @return `true` - `toString` method is overridden / `false` - otherwise.
   */
  public static boolean isToStringOverridden(Object object) {
    return getMethod(object.getClass(), "toString").getDeclaringClass() != Object.class;
  }
  
  private static RuntimeException wrap(ParseException e) {
    throw new RuntimeException(e);
  }
  
  public static <T> List<T> reverse(List<T> list) {
    ArrayList<T> reversed = new ArrayList<>(list);
    Collections.reverse(reversed);
    return reversed;
  }
  
  public record Entry<K, V>(K key, V value) {
    public static <K, V> Entry<K, V> $(K key, V value) {
      return new Entry<>(key, value);
    }
  }
}
