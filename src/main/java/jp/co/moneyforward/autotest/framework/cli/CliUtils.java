package jp.co.moneyforward.autotest.framework.cli;

import com.github.valid8j.pcond.forms.Predicates;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.platform.commons.support.ModifierSupport;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.valid8j.classic.Requires.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static jp.co.moneyforward.autotest.actions.web.SendKey.MASK_PREFIX;
import static org.junit.platform.commons.support.ReflectionSupport.invokeMethod;
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
  
  @SuppressWarnings({"RedundantCast"})
  public static List<String> listTags(String rootPackageName) {
    return ClassFinder.create(rootPackageName)
                      .findMatchingClasses(Predicates.alwaysTrue())
                      .map(c -> (Class<?>) c)
                      .flatMap((Function<Class<?>, Stream<Tag>>) CliUtils::tagAnnotationsFrom)
                      .map(e -> ((Tag) e).value()) // Workaround compilation error from IDEA.
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
  
  public static int runTests(String rootPackageName, String[] queries, String[] executionDescriptors, String[] executionProfile) {
    Map<Class<?>, TestExecutionSummary> testReport = runTests(rootPackageName, queries, executionDescriptors, executionProfile, new SummaryGeneratingListener());
    return testReport.values()
                     .stream()
                     .map(s -> s.getFailures().size())
                     .reduce(Integer::sum)
                     .orElseThrow(NoSuchElementException::new);
  }
  
  @SuppressWarnings({"unchecked", "RedundantCast"})
  public static Map<Class<?>, TestExecutionSummary> runTests(String rootPackageName, String[] queries, String[] executionDescriptors, String[] executionProfile, SummaryGeneratingListener testExecutionListener) {
    if (executionDescriptors.length > 0)
      System.setProperty("jp.co.moneyforward.autotest.scenes", composeSceneDescriptorPropertyValue(executionDescriptors));
    AutotestEngine.configureLoggingForSessionLevel();
    initialize(executionProfile);
    logExecutionProfile(CawebAccessingModel.executionProfile());
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
  
  public static void logExecutionProfile(ExecutionProfile executionProfile) {
    LOGGER.info("Execution Profile");
    LOGGER.info("----");
    Arrays.stream(executionProfile.getClass().getMethods())
          .filter(ModifierSupport::isPublic)
          .filter(m -> m.getParameters().length == 0)
          .filter(m -> !ModifierSupport.isStatic(m))
          .filter(m -> Objects.equals(executionProfile.getClass(), m.getDeclaringClass()))
          .sorted(Comparator.comparing(Method::getName))
          .forEach(m -> LOGGER.info(String.format("- %-30s -> %-30s", m.getName(), mask(invokeMethod(m, executionProfile)))));
    LOGGER.info("----");
    LOGGER.info("");
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
                                                       shorten(f.getException()
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
  
  private static String shorten(String string) {
    int crPos = string.indexOf('\r');
    return string.substring(0, Math.min(120,
                                        crPos < 0 ? string.length()
                                                  : crPos - 1));
  }
  
  private static String mask(Object o) {
    return Objects.toString(o).replaceAll("((" + MASK_PREFIX + ").*)", MASK_PREFIX);
  }
}
