package jp.co.moneyforward.autotest.ca_web.cli;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import jp.co.moneyforward.autotest.ca_web.tests.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.tests.Index;
import jp.co.moneyforward.autotest.framework.utils.Transform;
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

public interface ClassFinder {
  
  Stream<Class<?>> findMatchingClasses(Predicate<Class<?>> query);
  
  static ClassFinder create(String rootPackage) {
    return query -> {
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
  
  static Predicate<Class<?>> classNameIsEqualTo(String value) {
    return Transform.$(functionCanonicalName())
                    .check(isEqualTo(value));
  }
  
  static Predicate<Class<?>> classNameContaining(String value) {
    return Transform.$(functionCanonicalName())
                    .check(containsString(value));
  }
  
  static Predicate<Class<?>> classNameMatchesRegex(String value) {
    return Transform.$(functionCanonicalName())
                    .check(Predicates.matchesRegex(value));
  }
  
  static Predicate<Class<?>> alwaysTrue() {
    return Predicates.alwaysTrue();
  }
  
  static Predicate<Class<?>> isAssignableTo(Class<?> klass) {
    return Printables.predicate("isAssignableTo[" + klass.getSimpleName() + "]",
                                klass::isAssignableFrom);
  }
  
  @SafeVarargs
  static Predicate<Class<?>> hasTags(Class<? extends Annotation>... annotationClasses) {
    return Printables.predicate("hasAnnotation" + Arrays.stream(annotationClasses).map(c -> "@" + c.getSimpleName()).toList(),
                                (Class<?> c) -> Arrays.stream(annotationClasses).anyMatch(c::isAnnotationPresent));
  }
  
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
  
  private static Function<Class<?>, String> functionCanonicalName() {
    return function("canonicalName", Class::getSimpleName);
  }
  
  static void main(String... args) {
    {
      System.out.println("====");
      Predicate<Class<?>> query = alwaysTrue();
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = classNameMatchesRegex(".*Test");
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = classNameContaining("Test");
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = isAssignableTo(CawebAccessingModel.class);
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = hasTags(Tags.class);
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = hasTags(Tag.class);
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = hasTagValueEqualTo("bank");
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = hasTagValueContaining("smok");
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = hasTagValueMatchesRegex("smok.");
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
    {
      System.out.println("====");
      Predicate<Class<?>> query = classNameIsEqualTo(Index.class.getCanonicalName());
      System.out.println(query);
      create(Index.class.getPackageName()).findMatchingClasses(query)
                                          .forEach(System.out::println);
      System.out.println("----");
    }
  }
}
