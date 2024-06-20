package jp.co.moneyforward.autotest.ca_web.cli;

import com.github.valid8j.pcond.forms.Predicates;
import jp.co.moneyforward.autotest.ca_web.tests.Index;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.valid8j.classic.Requires.requireNonNull;

@Command(
    name = "autotest-cli", mixinStandardHelpOptions = true,
    version = "0.0",
    description = "A command line interface of 'autotest-ca', an automated testing tool for 'caweb'.")
public class Cli implements Callable<Integer> {
  
  private static final String ROOT_PACKAGE_NAME = Index.class.getPackageName();
  
  @Parameters(index = "0..*", description = "Subcommands of this CLI.")
  private List<String> subcommands;
  
  @Option(
      names = {"-q", "--query"},
      description = """
          Specifies a query. If multiple options are give, they will be treated as disjunctions.
          
          QUERY      ::= QUERY_TERM
          QUERY_TERM ::= ATTR ':' OP COND
          ATTR       ::= ('classname'|'tag')
          OP         ::= ('=' | '~')
          COND       ::= ('*'|CLASS_NAME|TAG_NAME)
          CLASS_NAME ::= {Java-wise valid character sequence}
          TAG_NAME   ::= (Any string)
          
          This should be used with run, list-tests, and list-tags subcommands.
          
          NOTE:
            '=' (OP): Exact match
            '~' (OP): Partial match
          """,
      defaultValue = "classname:~.*")
  private String[] queries = new String[]{"classname:~.*"};
  
  @Option(names = {"--execution-descriptor"},
      description = """
          Used with 'run' subcommand.
          An execution descriptor is a JSON and it should look like following:
          
          {
            "beforeAll": ["open"],
            "beforeEach": [],
            "tests": ["login", "connectBank", "disconnectBank", "logout"]
            "afterEach": ["screenshot"],
            "afterAll": ["close"],
          }
          
          This option can be specified multiple times.
          If there are more than one, all the combinations between the specified execution descriptors and selected access models (or access models of tests) will be executed.
          
          When the descriptor is executed with an access model class, it will specify the scenario to be performed using the model.
          When it is executed with a test class, it overrides the @AutotestExecution annotation, which is attached to a test class, if an element in a result set of -q, --query options.
          
          NOTE: Not yet implemented!
          """)
  private String[] executionDescriptors = new String[]{};
  
  @Option(names = {"--execution-profile"},
      description = """
          Used with 'run' subcommand.
          
          Specifies an execution profile, with which you can override a test's execution time parameters such as: user email, password, etc.
          
          NOTE: Not yet implemented!
          """)
  private String executionProfile = "{}";
  
  
  @SuppressWarnings("unchecked")
  @Command(name = "run",
      description = {"""
          Runs tests matching with any of -q, --query options.
          
          Even if one test matches with multiple -q, -query options, it will be executed only once.
          """})
  public Integer run() {
    int ret;
    try {
      AtomicInteger failed = new AtomicInteger(0);
      ClassFinder.create(ROOT_PACKAGE_NAME)
                 .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                          .map(Cli::parse)
                                                          .map(p -> p.and(ClassFinder.hasTags(AutotestExecution.class)))
                                                          .toArray(Predicate[]::new)))
                 .map(c -> (Class<?>) c)
                 .forEach(new Consumer<Class<?>>() {
                   @Override
                   public void accept(Class<?> c) {
                     TestExecutionSummary testExecutionSummary = TestClassRunner.create()
                                                                                .runTestClass((Class<?>) c);
                     failed.set(failed.get() + testExecutionSummary.getFailures().size());
                     testExecutionSummary.printTo(new PrintWriter(System.err));
                   }
                 });
      ret = failed.get() == 0 ? 0
                              : 1;
    } catch (IllegalArgumentException e) {
      ret = 2;
    }
    return ret;
  }
  
  @SuppressWarnings("unchecked")
  @Command(
      name = "list-tests",
      description = """
          Prints all known tests.
          A <testname> in the result can be used in a "classname:<testname>" given to -q, --query options
          """)
  public Integer listTests() {
    int ret;
    try {
      ClassFinder.create(ROOT_PACKAGE_NAME)
                 .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                          .map(Cli::parse)
                                                          .map(p -> p.and(ClassFinder.hasTags(AutotestExecution.class)))
                                                          .toArray(Predicate[]::new)))
                 .forEach(System.out::println);
      ret = 0;
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      ret = 2;
    }
    return ret;
  }
  
  @SuppressWarnings("unchecked")
  @Command(name = "list-tags",
      description = """
          Prints all known tags.
          A <tag> in the result can be used in a "tag:<tag>" query given to -q, --query= options.
          """)
  public Integer listTags() {
    int ret;
    try {
      ClassFinder.create(ROOT_PACKAGE_NAME)
                 .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                          .map(Cli::parse)
                                                          .map(p -> p.and(ClassFinder.hasTags(Tag.class, Tags.class)))
                                                          .toArray(Predicate[]::new)))
                 .map(c -> (Class<?>) c)
                 .flatMap((Function<Class<?>, Stream<? extends Tag>>) Cli::getConcat)
                 .map(new Function<Tag, String>() {
                   @Override
                   public String apply(Tag tag) {
                     return tag.value();
                   }
                 })
                 .distinct()
                 .forEach(System.out::println);
      ret = 0;
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      ret = 2;
    }
    return ret;
  }
  
  private static Stream<Tag> getConcat(Class<?> c) {
    return Stream.concat(
        streamTags(c),
        getB(c));
  }
  
  private static Stream<Tag> getB(Class<?> c) {
    return c.isAnnotationPresent(Tags.class) ? Arrays.<Tag>stream((Tag[]) c.getAnnotation(Tags.class).value())
                                             : Stream.<Tag>empty();
  }
  
  private static Stream<Tag> streamTags(Class<?> c) {
    return c.isAnnotationPresent(Tag.class) ? Stream.of(c.getAnnotation(Tag.class))
                                            : Stream.empty();
  }
  
  @Command(
      name = "list-accessmodels",
      description = """
          Prints all known access models.
          An <accessmodel> in the result can be used in a "accessmodel:<tag>" query given to -q, --query options.
          
          NOTE: Not yet implemented!
          """)
  public Integer listAccessModels() {
    throw new UnsupportedOperationException();
  }
  
  @Command(
      name = "show-default-execution-descriptors",
      description = {"""
          Show default execution descriptors of tests matching with any of -q, --query options.
          
          Even if one test matches with multiple -q, -query options, it will be shown only once.
          
          NOTE: Not yet implemented!
          """})
  public Integer showDefaultExecutionDescriptors() {
    throw new UnsupportedOperationException();
  }
  
  @Command(
      name = "show-default-execution-profile",
      description = {"""
          Show default execution profile.
          
          NOTE: Not yet implemented!
          """})
  public Integer showExecutionProfile() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Integer call() throws Exception { // your business logic goes here...
    System.err.println("You didn't specify known subcommands, try -h, --help option: " + this.subcommands);
    return 2;
  }
  
  public static void main(String... args) {
    int exitCode = 1;
    try {
      exitCode = new CommandLine(new Cli()).execute(args);
    } finally {
      System.exit(exitCode);
    }
  }
  
  private static Predicate<Class<?>> parse(String query) {
    requireNonNull(query);
    Pattern regex = Pattern.compile("(?<attr>classname|tag):(?<op>[=~]?)(?<cond>.*)");
    Matcher matcher = regex.matcher(query);
    if (!matcher.matches())
      throw new IllegalArgumentException("A query '" + query + "' didn't match: " + regex);
    String attr = matcher.group("attr");
    String op_ = matcher.group("op");
    String op = !Objects.equals(op_, "") ? op_
                                         : "=";
    String cond = matcher.group("cond");
    switch (attr) {
      case "classname":
        switch (op) {
          case "=":
            return ClassFinder.classNameIsEqualTo(cond);
          case "~":
            return ClassFinder.classNameMatchesRegex(cond);
          case "%":
            return ClassFinder.classNameContaining(cond);
          default:
            assert false;
        }
      case "tag":
        switch (op) {
          case "=":
            return ClassFinder.hasTagValueEqualTo(cond);
          case "~":
            return ClassFinder.hasTagValueMatchesRegex(cond);
          case "%":
            return ClassFinder.hasTagValueContaining(cond);
          default:
            assert false;
        }
      default:
        assert false;
    }
    throw new AssertionError();
  }
}