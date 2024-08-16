package jp.co.moneyforward.autotest.framework.utils;

import com.github.dakusui.actionunit.actions.Composite;
import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.valid8j.pcond.forms.Printables;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.AutotestException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.IOException;
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
  
  /**
   * Returns an `Optional` of a `String` that contains a branch name.
   * This method internally calls `InternalUtils#currentBranchNameFor(new File("."))`.
   *
   * @return An `Optional` of branch name `String`.
   * @see InternalUtils#currentBranchNameFor(File)
   */
  public static Optional<String> currentBranchName() {
    return currentBranchNameFor(projectDir());
  }
  
  /**
   * Returns an `Optional` of a `String` that contains a branch name, if the given `projectDir` has `.git` directory and a current branch name of it can be retrieved.
   * An exception will be thrown on a failure during this step.
   *
   * Otherwise, an empty `Optional` will be returned.
   *
   * @return An `Optional` of branch name `String`.
   */
  public static Optional<String> currentBranchNameFor(File projectDir) {
    try {
      File gitDir = new File(projectDir, ".git");
      if (!gitDir.exists())
        return Optional.empty();
      //NOSONAR
      try (Repository repository = new FileRepositoryBuilder()
          .setMustExist(true)
          .setGitDir(gitDir)
          .readEnvironment()
          .build()) {
        return Optional.of(repository.getBranch());
      }
    } catch (IOException e) {
      throw wrap(e);
    }
  }
  
  
  public static boolean isPresumablyRunningFromIDE() {
    return !isRunByTool();
  }
  
  private static boolean isRunByTool() {
    return isRunByGithubActions()
        || isRunUnderPitest()
        || isRunUnderSurefire();
  }
  
  private static boolean isRunByGithubActions() {
    return !Objects.equals(System.getenv("GITHUB_ACTIONS"), null);
  }
  
  public static boolean isRunUnderSurefire() {
    return System.getProperty("surefire.real.class.path") != null;
  }
  
  public static boolean isRunUnderPitest() {
    return Objects.equals(System.getProperty("underpitest"), "yes");
  }
  
  public static String composeResultMessageLine(Class<?> testClass, String stageName, String line) {
    return String.format("%-20s: %-11s %s", testClass.getSimpleName(), stageName + ":", line);
  }
  
  public static File projectDir() {
    return new File(".");
  }
  
  @SafeVarargs
  public static <T> Scene chainActs(String variableName, LeafAct<T, T>... acts) {
    Scene.Builder builder = new Scene.Builder(variableName);
    for (LeafAct<T, T> act : acts) {
      builder = builder.add(act);
    }
    return builder.build();
  }
  
  public static Stream<Action> flattenIfSequential(Action a) {
    return a instanceof Composite composite && !composite.isParallel() ? ((Composite) a).children().stream()
                                                                       : Stream.of(a);
  }
  
  /**
   * A shorthand method of `shorten(string, 120)`.
   *
   * @param string A string to be shortened.
   * @return A shortened string.
   */
  public static String shorten(String string) {
    return shorten(string, 120);
  }
  
  /**
   * Shorten a `string` to the specified `length`.
   * In case `string` contains  a carriage return (`\r`), a substring from the beginning of the `string` to the position
   * of the character will be returned.
   *
   * @param string A string to be shortened.
   * @param length A length to which `string` to be shortened.
   * @return A shortened string.
   */
  public static String shorten(String string, int length) {
    int crPos = string.indexOf('\r');
    return string.substring(0, Math.min(length,
                                        crPos < 0 ? string.length()
                                                  : crPos));
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
  public static Date now() {
    return new Date();
  }
  
  public static String dateToSafeString(Date date) {
    return new SimpleDateFormat("HHmmss", Locale.US).format(date).replaceAll("[,. :\\-/]", "");
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
        return consumerName.replace("\n", " ");
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
   * Checks if the given `object` has a `toString` method which overrides `Object#toString`.
   *
   * @param object An object to be checked.
   * @return `true` - `toString` method is overridden / `false` - otherwise.
   */
  public static boolean isToStringOverridden(Object object) {
    return getMethod(object.getClass(), "toString").getDeclaringClass() != Object.class;
  }
  
  /**
   * // @formatter:off
   * Wraps a given exception `e` with a framework specific exception, `AutotestException`.
   *
   * This method has `RuntimeException` as return value type, however, this method will never return a value but throws an exception.
   * The return type is defined to be able to write a caller code in the following style, which increases readability.
   *
   * ```java
   * try {
   *   doSomthing()
   * } catch (SomeCheckedException e) {
   *   throw wrap(e);
   * }
   * ```
   *
   * If a given exception `e` is a `RuntimeException`, or an `Error`, it will not be wrapped, but `e` will be directly thrown.
   *
   * // @formatter:on
   *
   * @param e An exception to be wrapped.
   * @return This method will never return any value.
   */
  public static RuntimeException wrap(Throwable e) {
    if (e instanceof RuntimeException exception) {
      throw exception;
    }
    if (e instanceof Error error) {
      throw error;
    }
    throw new AutotestException("Exception was cause: [" + e.getClass().getSimpleName() + "]: " + e.getMessage(), e);
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
