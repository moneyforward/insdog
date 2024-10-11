package jp.co.moneyforward.autotest.framework.cli;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jp.co.moneyforward.autotest.framework.utils.Valid8JCliches;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.valid8j.pcond.forms.Predicates.containsString;
import static com.github.valid8j.pcond.forms.Predicates.isEqualTo;
import static com.github.valid8j.pcond.forms.Printables.function;

/**
 * An interface to find classes on the class-path.
 */
public interface ClassFinder {
  /**
   * Returns a stream of classes on the class-path, each of which matches the `query`.
   *
   * @param query A query to check if a given class matches.
   *
   * @return A stream of matching classes.
   */
  Stream<Class<?>> findMatchingClasses(Predicate<Class<?>> query);
  
  /**
   * Returns a `ClassFinder`, which checks all the classes under `rootPackage` with a given `query` predicate.
   *
   * @param rootPackage A root package.
   * @return A new `ClassFinder` object.
   */
  static ClassFinder create(String rootPackage) {
    return query -> {
      //NOSONAR
      try (ScanResult result = new ClassGraph().enableClassInfo()
                                               .enableAnnotationInfo()
                                               .acceptPackages(rootPackage)
                                               .scan()) {
        
        return result.getAllClasses()
                     .stream()
                     .map(ClassInfo::loadClass)
                     .filter(query)
                     .toList()
                     .stream()
                     .map(c -> (Class<?>) c);
      }
    };
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param value A string value from which the returned predicate is created.
   * @return A predicate which matches a class whose name is equal to `value`.
   */
  static Predicate<Class<?>> classNameIsEqualTo(String value) {
    return Valid8JCliches.Transform.$(functionSimpleName())
                                   .check(isEqualTo(value));
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param value A string value from which the returned predicate is created.
   * @return A predicate which matches a class whose name is containing `value`.
   */
  static Predicate<Class<?>> classNameContaining(String value) {
    return Valid8JCliches.Transform.$(functionSimpleName())
                                   .check(containsString(value));
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param value A string value from which the returned predicate is created.
   * @return A predicate which matches a class whose name matches a regular expression `value`.
   */
  static Predicate<Class<?>> classNameMatchesRegex(String value) {
    return Valid8JCliches.Transform.$(functionSimpleName())
                                   .check(Predicates.matchesRegex(value));
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @return A predicate which matches any class.
   */
  static Predicate<Class<?>> alwaysTrue() {
    return Predicates.alwaysTrue();
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param klass A class object from which the returned predicate is created.
   * @return A predicate which matches a class which is assignable from `klass`.
   */
  static Predicate<Class<?>> isAssignableTo(Class<?> klass) {
    return Printables.predicate("isAssignableTo[" + klass.getSimpleName() + "]",
                                klass::isAssignableFrom);
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param annotationClasses classes from which the returned predicate is created.
   * @return A predicate which matches any of `annotationClasses` is present.
   */
  @SafeVarargs
  static Predicate<Class<?>> hasAnnotations(Class<? extends Annotation>... annotationClasses) {
    return Printables.predicate("hasAnnotation" + Arrays.stream(annotationClasses).map(c -> "@" + c.getSimpleName()).toList(),
                                (Class<?> c) -> Arrays.stream(annotationClasses).anyMatch(c::isAnnotationPresent));
  }
  
  /**
   * A utility method to return a `Predicate`, which can be used with `findMatchingClasses` method.
   *
   * @param value A string value from which the returned predicate is created.
   * @return A predicate which matches a class whose `@Tag` annotation value is equal to `value`.
   */
  static Predicate<Class<?>> hasTagValueEqualTo(String value) {
    return hasTagMatching(Printables.predicate("valueEqualTo[" + value + "]", t -> Objects.equals(t.value(), value)));
  }
  
  private static Predicate<Class<?>> hasTagMatching(Predicate<Tag> predicate) {
    return Printables.predicate("hasTagMatching[" + predicate + "]",
                                aClass -> aClass.isAnnotationPresent(Tag.class) && predicate.test(aClass.getAnnotation(Tag.class)) ||
                                    aClass.isAnnotationPresent(Tags.class) && Arrays.stream(aClass.getAnnotation(Tags.class).value())
                                                                                    .anyMatch(predicate));
  }
  
  static Predicate<Class<?>> hasTagValueContaining(String value) {
    return hasTagMatching(Printables.predicate("valueContaining[" + value + "]", t -> t.value().contains(value)));
  }
  
  static Predicate<Class<?>> hasTagValueMatchesRegex(String value) {
    return hasTagMatching(Printables.predicate("valueMatchingRegex[" + value + "]", t -> t.value().matches(value)));
  }
  
  static Function<Class<?>, String> functionSimpleName() {
    return function("simpleName", Class::getSimpleName);
  }
}
