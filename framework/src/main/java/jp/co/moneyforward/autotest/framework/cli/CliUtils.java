package jp.co.moneyforward.autotest.framework.cli;

import com.github.valid8j.pcond.forms.Predicates;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment.testResultDirectoryFor;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.removeFile;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.writeTo;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A utility class for **autotest** CLI.
 */
public enum CliUtils {
  ;
  private static final Logger LOGGER = LoggerFactory.getLogger(CliUtils.class);
  private static Map<String, String> profileOverriders;
  
  static void initialize(String[] profileOverriders) {
    CliUtils.profileOverriders = Arrays.stream(profileOverriders).collect(toMap(each -> each.substring(each.indexOf('=') + 1, each.indexOf(':')),
                                                                                each -> each.substring(each.indexOf(':') + 1)));
  }
  
  public static Map<String, String> getProfileOverriders() {
    return profileOverriders;
  }
  
  public static List<String> listTags(String rootPackageName) {
    return ClassFinder.create(rootPackageName)
                      .findMatchingClasses(Predicates.alwaysTrue())
                      .map(c -> (Class<?>) c)
                      .flatMap((Function<Class<?>, Stream<Tag>>) CliUtils::tagAnnotationsFrom)
                      .map(Tag::value) // Workaround compilation error from IDEA.
                      .distinct()
                      .toList();
  }
  
  @SuppressWarnings("unchecked")
  public static List<Class<?>> listTestClasses(String[] queries1, String rootPackageName) {
    return ClassFinder.create(rootPackageName)
                      .findMatchingClasses(Predicates.or(Arrays.stream(queries1)
                                                               .map(CliUtils::parseQuery)
                                                               .map(p -> p.and(ClassFinder.hasAnnotations(AutotestExecution.class)))
                                                               .toArray(Predicate[]::new)))
                      .toList();
  }
  
  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public static Predicate<Class<?>> parseQuery(String query) {
    requireNonNull(query);
    Pattern regex = Pattern.compile("(?<attr>classname|tag):(?<op>[=~%]?)(?<cond>.*)");
    Matcher matcher = regex.matcher(query);
    if (!matcher.matches())
      throw new IllegalArgumentException("A query '" + query + "' didn't match: " + regex);
    String attr = matcher.group("attr");
    String opInQuery = matcher.group("op");
    String op = !Objects.equals(opInQuery, "") ? opInQuery
                                               : "(default)";
    String cond = matcher.group("cond");
    // We do use switch as the attribute names are planned to be enhanced to provide new features.
    return switch (attr) {
      case "classname" -> switch (op) {
        case "~" -> ClassFinder.classNameMatchesRegex(cond);
        case "%" -> ClassFinder.classNameContaining(cond);
        // '=' and 'default' are handled as 'equalTo' operator
        default -> ClassFinder.classNameIsEqualTo(cond);
      };
      // "tag" attribute is handled by this clause.
      default -> switch (op) {
        case "~" -> ClassFinder.hasTagValueMatchesRegex(cond);
        case "%" -> ClassFinder.hasTagValueContaining(cond);
        // '=' and 'default' are handled as 'equalTo' operator
        default -> ClassFinder.hasTagValueEqualTo(cond);
      };
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
  
  public static String composeSceneDescriptorPropertyValue(String[] executionDescriptors) {
    Map<String, List<String>> map = new HashMap<>();
    for (var each : executionDescriptors) {
      var e = each.split("=");
      map.putIfAbsent(e[0], Collections.emptyList());
      map.computeIfPresent(e[0], (k, v) -> Stream.concat(v.stream(),
                                                         Stream.of(e[1].split(","))).toList());
    }
    return "inline:" + map.entrySet()
                          .stream()
                          .map(e -> String.format("%s=%s", e.getKey(), String.join(",", e.getValue())))
                          .collect(joining(";"));
  }
  
  public static int runTests(String rootPackageName, String[] queries, String[] executionDescriptors, String[] executionProfile) {
    Map<Class<?>, TestExecutionSummary> testReport = runTests(rootPackageName,
                                                              queries,
                                                              executionDescriptors,
                                                              executionProfile,
                                                              createSummaryGeneratingListener()
    );
    return testReport.values()
                     .stream()
                     .map(s -> s.getFailures().size())
                     .reduce(Integer::sum)
                     .orElseThrow(NoSuchElementException::new);
  }
  
  public static SummaryGeneratingListener createSummaryGeneratingListener() {
    return new SummaryGeneratingListener() {
      long before;
      
      @Override
      
      public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest() || isTestClass(testIdentifier)) {
          before = System.currentTimeMillis();
          File resultFile = resultFileFor(testIdentifier);
          removeFile(resultFile);
          writeTo(resultFile, String.format("TYPE: %s%n", testIdentifier.getType()));
        }
        super.executionStarted(testIdentifier);
      }
      
      @Override
      public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        super.executionFinished(testIdentifier, testExecutionResult);
        if (testIdentifier.isTest() || isTestClass(testIdentifier)) {
          File resultFile = resultFileFor(testIdentifier);
          writeTo(resultFile, String.format("TIME: %d%n", System.currentTimeMillis() - before));
          writeTo(resultFile, String.format("RESULT: %s%n", testExecutionResult.getStatus()));
        }
      }
      
      private File resultFileFor(TestIdentifier testIdentifier) {
        return new File(testResultDirectoryFor(testClassNameOf(testIdentifier),
                                               testIdentifier.getDisplayName()).toFile(), "RESULT");
      }
      
      private String testClassNameOf(TestIdentifier testIdentifier) {
        return testIdentifier.getUniqueIdObject()
                             .getSegments()
                             .stream()
                             .filter(s -> Objects.equals(s.getType(), "class"))
                             .map(UniqueId.Segment::getValue)
                             .findFirst()
                             .orElse("unknown.TestClass");
      }
      
      private boolean isTestClass(TestIdentifier testIdentifier) {
        return testIdentifier.getType() == TestDescriptor.Type.CONTAINER
            && testClassNameOf(testIdentifier).endsWith(testIdentifier.getDisplayName());
      }
    };
  }
  
  @SuppressWarnings({"unchecked", "RedundantCast"})
  public static Map<Class<?>, TestExecutionSummary> runTests(String rootPackageName,
                                                             String[] queries,
                                                             String[] executionDescriptors,
                                                             String[] executionProfile,
                                                             SummaryGeneratingListener testExecutionListener) {
    if (executionDescriptors.length > 0)
      System.setProperty("jp.co.moneyforward.autotest.scenes", composeSceneDescriptorPropertyValue(executionDescriptors));
    AutotestEngine.configureLoggingForSessionLevel();
    initialize(executionProfile);
    Map<Class<?>, TestExecutionSummary> testReport = new HashMap<>();
    List<Class<?>> targetTestClasses = new ArrayList<>();
    Launcher launcher = LauncherFactory.create();
    LauncherDiscoveryRequestBuilder requestBuilder = request();
    ClassFinder.create(rootPackageName)
               .findMatchingClasses(Predicates.or(Arrays.stream(queries)
                                                        .map(CliUtils::parseQuery)
                                                        .map(p -> p.and(ClassFinder.hasAnnotations(AutotestExecution.class)))
                                                        .toArray(Predicate[]::new)))
               .map(c -> (Class<?>) c)
               // We need this cast (Class<?>o) to work around a presumable compiler bug in Java 21.
               .sorted(Comparator.comparing(o -> ((Class<?>) o).getCanonicalName()))
               .forEach((Consumer<Class<?>>) c -> {
                 requestBuilder.selectors(selectClass(c));
                 targetTestClasses.add(c);
               });
    LOGGER.info("Running test classes in {}", rootPackageName);
    LOGGER.info("----");
    targetTestClasses.forEach(c -> LOGGER.info("- {}", c.getCanonicalName()));
    LOGGER.info("----");
    LOGGER.info("");
    
    launcher.execute(requestBuilder.build(), testExecutionListener);
    TestExecutionSummary testExecutionSummary = testExecutionListener.getSummary();
    testReport.put(CliUtils.class, testExecutionSummary);
    logExecutionSummary(testExecutionSummary);
    logFailureSummary(testExecutionSummary);
    return testReport;
  }
  
  private static void logExecutionSummary(TestExecutionSummary testExecutionSummary) {
    LOGGER.info("----");
    StringWriter buffer = new StringWriter();
    testExecutionSummary.printTo(new PrintWriter(buffer));
    Arrays.stream(buffer.toString()
                        .split("\n"))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .forEach(LOGGER::info);
    LOGGER.info("----");
    LOGGER.info("");
  }
  
  private static void logFailureSummary(TestExecutionSummary testExecutionSummary) {
    LOGGER.info("Failure summary");
    LOGGER.info("----");
    if (testExecutionSummary.getFailures().isEmpty()) LOGGER.info("- (none)");
    else testExecutionSummary.getFailures()
                             .forEach(f -> LOGGER.info("- {}(in {}): {}",
                                                       f.getTestIdentifier().getDisplayName(),
                                                       Optional.ofNullable(segmentsOfExecutionSummary(f).size() >= 2 ? segmentsOfExecutionSummary(f).get(1)
                                                                                                                     : null)
                                                               .map(UniqueId.Segment::getValue)
                                                               .orElse("unknown"),
                                                       InternalUtils.shorten(f.getException()
                                                                              .getMessage()
                                                                              .replace("\n", " "))));
    LOGGER.info("----");
    LOGGER.info("");
  }
  
  private static List<UniqueId.Segment> segmentsOfExecutionSummary(TestExecutionSummary.Failure f) {
    return f.getTestIdentifier()
            .getUniqueIdObject()
            .getSegments();
  }
}
