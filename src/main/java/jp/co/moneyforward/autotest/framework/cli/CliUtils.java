package jp.co.moneyforward.autotest.framework.cli;

import com.github.valid8j.pcond.forms.Predicates;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A utility class for **autotest** CLI.
 */
public enum CliUtils {
  ;
  private static final Logger LOGGER = LoggerFactory.getLogger(CliUtils.class);
  
  @SuppressWarnings({"RedundantCast", "unchecked"})
  static List<String> listTags(String[] queries, String rootPackageName) {
    return ClassFinder.create(rootPackageName)
                      .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                               .map(CliUtils::parseQuery)
                                                               .map(p -> p.and(ClassFinder.hasTags(Tag.class, Tags.class)))
                                                               .toArray(Predicate[]::new)))
                      .map(c -> (Class<?>) c)
                      .flatMap((Function<Class<?>, Stream<Tag>>) CliUtils::tagAnnotationsFrom)
                      .map(e -> ((Tag) e).value()) // Workaround compilation error from IDEA.
                      .distinct()
                      .toList();
  }
  
  @SuppressWarnings("unchecked")
  static List<Class<?>> listTestClasses(String[] queries1, String rootPackageName) {
    return ClassFinder.create(rootPackageName)
                      .findMatchingClasses(Predicates.or(Arrays.stream(queries1)
                                                               .map(CliUtils::parseQuery)
                                                               .map(p -> p.and(ClassFinder.hasTags(AutotestExecution.class)))
                                                               .toArray(Predicate[]::new)))
                      .toList();
  }
  
  public static Predicate<Class<?>> parseQuery(String query) {
    requireNonNull(query);
    Pattern regex = Pattern.compile("(?<attr>classname|tag):(?<op>[=~%]?)(?<cond>.*)");
    Matcher matcher = regex.matcher(query);
    if (!matcher.matches())
      throw new IllegalArgumentException("A query '" + query + "' didn't match: " + regex);
    String attr = matcher.group("attr");
    String opInQuery = matcher.group("op");
    String op = !Objects.equals(opInQuery, "") ? opInQuery
                                               : "=";
    String cond = matcher.group("cond");
    return switch (attr) {
      case "classname" -> switch (op) {
        case "=" -> ClassFinder.classNameIsEqualTo(cond);
        case "~" -> ClassFinder.classNameMatchesRegex(cond);
        case "%" -> ClassFinder.classNameContaining(cond);
        default -> throw new AssertionError();
      };
      case "tag" -> switch (op) {
        case "=" -> ClassFinder.hasTagValueEqualTo(cond);
        case "~" -> ClassFinder.hasTagValueMatchesRegex(cond);
        case "%" -> ClassFinder.hasTagValueContaining(cond);
        default -> throw new AssertionError();
      };
      default -> throw new AssertionError();
    };
  }
  
  public static Stream<Tag> tagAnnotationsFrom(Class<?> c) {
    return Stream.concat(
        streamTags(c),
        tagAnnotationsFromParentContainer(c));
  }
  
  public static Stream<Tag> tagAnnotationsFromParentContainer(Class<?> c) {
    return c.isAnnotationPresent(Tags.class) ? Arrays.stream(c.getAnnotation(Tags.class).value())
                                             : Stream.empty();
  }
  
  public static Stream<Tag> streamTags(Class<?> c) {
    return c.isAnnotationPresent(Tag.class) ? Stream.of(c.getAnnotation(Tag.class))
                                            : Stream.empty();
  }
  
  static String composeSceneDescriptorPropertyValue(String[] executionDescriptors) {
    Map<String, List<String>> map = new HashMap<>();
    for (var each : executionDescriptors) {
      var e = each.split("=");
      map.putIfAbsent(e[0], Collections.emptyList());
      map.computeIfPresent(e[0], (k, v) -> Stream.concat(v.stream(),
                                                         Stream.of(e[1].split(","))).toList());
    }
    StringBuilder b = new StringBuilder("inline:");
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      b.append(String.format("%s=%s", entry.getKey(), String.join(",", entry.getValue())));
    }
    return b.toString();
  }
  
  public static int runTests(String rootPackageName, String[] queries, String[] executionDescriptors) {
    Map<Class<?>, TestExecutionSummary> testReport = runTests(rootPackageName, queries, executionDescriptors, new SummaryGeneratingListener());
    return testReport.values()
                     .stream()
                     .map(s -> s.getFailures().size())
                     .reduce(Integer::sum)
                     .orElseThrow(NoSuchElementException::new);
  }
  
  @SuppressWarnings("unchecked")
  public static Map<Class<?>, TestExecutionSummary> runTests(String rootPackageName, String[] queries, String[] executionDescriptors, SummaryGeneratingListener testExecutionListener) {
    if (executionDescriptors.length > 0)
      System.setProperty("jp.co.moneyforward.autotest.scenes", composeSceneDescriptorPropertyValue(executionDescriptors));
    Map<Class<?>, TestExecutionSummary> testReport = new HashMap<>();
    ClassFinder.create(rootPackageName)
               .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                        .map(CliUtils::parseQuery)
                                                        .map(p -> p.and(ClassFinder.hasTags(AutotestExecution.class)))
                                                        .toArray(Predicate[]::new)))
               .map(c -> (Class<?>) c)
               .forEach((Consumer<Class<?>>) c -> {
                 LOGGER.info("Running tests in:[{}]", c.getCanonicalName());
                 LOGGER.info("----");
                 runTestClass(testExecutionListener, c);
                 TestExecutionSummary testExecutionSummary = testExecutionListener.getSummary();
                 testReport.put(c, testExecutionSummary);
                 StringWriter buffer = new StringWriter();
                 testExecutionSummary.printTo(new PrintWriter(buffer));
                 LOGGER.info(buffer.toString());
               });
    return testReport;
  }
  
  private static void runTestClass(SummaryGeneratingListener additionalListener, Class<?> testClass) {
    Launcher launcher = LauncherFactory.create();
    LauncherDiscoveryRequest request = request().selectors(selectClass(testClass))
                                                .build();
    launcher.execute(request, additionalListener);
  }
}
