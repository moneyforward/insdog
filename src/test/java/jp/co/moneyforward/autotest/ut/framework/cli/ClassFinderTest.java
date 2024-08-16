package jp.co.moneyforward.autotest.ut.framework.cli;

import com.github.valid8j.pcond.forms.Predicates;
import com.github.valid8j.pcond.forms.Printables;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.tests.Index;
import jp.co.moneyforward.autotest.framework.cli.ClassFinder;
import jp.co.moneyforward.autotest.framework.utils.Valid8JCliches;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.valid8j.fluent.Expectations.*;
import static com.github.valid8j.pcond.forms.Predicates.*;
import static jp.co.moneyforward.autotest.framework.cli.ClassFinder.*;

public class ClassFinderTest {
  @Test
  public void whenFindMatchingClassUnderRootPackage_thenFoundAll() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = Predicates.alwaysTrue();
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionCanonicalName())
                                                          .check(containsString(Index.class.getPackageName()))));
  }
  
  @Test
  public void whenFindClassesUsingClassNameRegex_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = classNameMatchesRegex(".*Test");
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionCanonicalName())
                                                          .check(containsString(Index.class.getPackageName()).and(endsWith("Test")))));
  }
  
  @Test
  public void whenFindClassesUsingClassNameRegexWithAlwaysTrue_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = classNameMatchesRegex(".*Test").and(ClassFinder.alwaysTrue());
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionCanonicalName())
                                                          .check(containsString(Index.class.getPackageName()).and(endsWith("Test")))));
  }

  @Test
  public void whenFindClassesUsingClassNamePartialMatch_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = classNameContaining("Test");
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionCanonicalName())
                                                          .check(containsString(Index.class.getPackageName()).and(containsString("Test")))));
  }
  
  @Test
  public void whenFindSubclasses_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = isAssignableTo(CawebAccessingModel.class);
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionCanonicalName())
                                                          .check(containsString(Index.class.getPackageName()))),
              value(out).stream()
                        .toBe()
                        .allMatch(predicateIsAssignableFrom(CawebAccessingModel.class)));
  }
  
  
  @Test
  public void whenFindTagsAnnotationPresence_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = hasAnnotations(Tags.class);
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    assertAll(value(out).toBe()
                        .notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(predicateIsAnnotationPresent(Tags.class)));
  }
  
  @Test
  public void whenFindClassesByTagPresence_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = hasAnnotations(Tag.class);
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    assertAll(value(out).toBe().notEmpty(),
              value(out).stream().toBe().allMatch(predicateIsAnnotationPresent(Tag.class)));
  }
  
  @Test
  public void whenFindClassesByTagValue_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = hasTagValueEqualTo("bank");
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe().notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionTags()).check(predicateAnyMatchInTagList(predicateTagValueIs("bank")))));
  }
  
  
  @Test
  public void whenFindTagValueWithPartialMatch_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = hasTagValueContaining("smok");
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    
    assertAll(value(out).toBe().notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionTags()).check(predicateAnyMatchInTagList(predicateTagValueContains("smok")))));
  }
  
  @Test
  public void whenFindTagValueWithRegex_thenMatchesFound() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = hasTagValueMatchesRegex("smok.");
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);

    assertAll(value(out).toBe().notEmpty(),
              value(out).stream()
                        .toBe()
                        .allMatch(Valid8JCliches.Transform.$(functionTags()).check(predicateAnyMatchInTagList(predicateTagValueMatchesRegex("smok.")))));
  }
  
  @Test
  public void whenFindClassesByNonMatchingClassNames_thenEmpty() {
    List<Class<?>> out = new LinkedList<>();
    Predicate<Class<?>> query = classNameIsEqualTo(Index.class.getCanonicalName());
    ClassFinder.create(Index.class.getPackageName())
               .findMatchingClasses(query)
               .forEach(out::add);
    assertStatement(value(out).toBe().empty());
  }
  
  private static Function<Class<?>, List<Tag>> functionTags() {
    return (Class<?> c) -> Stream.concat(
        c.isAnnotationPresent(Tag.class) ? Stream.of(c.getAnnotation(Tag.class))
                                         : Stream.empty(),
        c.isAnnotationPresent(Tags.class) ? Arrays.stream(c.getAnnotation(Tags.class).value())
                                          : Stream.empty()).toList();
  }
  
  private static Function<Class<?>, String> functionCanonicalName() {
    return Printables.function("canonicalName", Class::getCanonicalName);
  }
  
  @SuppressWarnings("SameParameterValue")
  private static Predicate<Class<?>> predicateIsAssignableFrom(Class<?> klass) {
    return Printables.predicate("isAssignableFrom", klass::isAssignableFrom);
  }
  
  private static Predicate<Class<?>> predicateIsAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return Printables.predicate("isAnnotationPresent[" + annotationClass.getSimpleName() + "]",
                                c -> c.isAnnotationPresent(annotationClass) || isAnnotationContainerPresent(annotationClass, c));
  }
  
  private static boolean isAnnotationContainerPresent(Class<? extends Annotation> annotationClass, Class<?> c) {
    return annotationClass.isAnnotationPresent(Repeatable.class) && c.isAnnotationPresent(annotationClass.getAnnotation(Repeatable.class).value());
  }
  
  @SuppressWarnings("SameParameterValue")
  private static Predicate<Tag> predicateTagValueIs(String expectedValue) {
    return Printables.predicate("tagValueIs[" + expectedValue + "]", i -> i.value().equals(expectedValue));
  }
  
  @SuppressWarnings("SameParameterValue")
  private static Predicate<Tag> predicateTagValueContains(String expectedValue) {
    return Printables.predicate("tagValueContains[" + expectedValue + "]", i -> i.value().contains(expectedValue));
  }
  
  @SuppressWarnings("SameParameterValue")
  private static Predicate<Tag> predicateTagValueMatchesRegex(String expectedValue) {
    return Printables.predicate("tagValueMatchesRegex[" + expectedValue + "]", i -> i.value().matches(expectedValue));
  }

  private static Predicate<List<Tag>> predicateAnyMatchInTagList(Predicate<Tag> valueIs) {
    return Printables.predicate("anyMatch[" + valueIs + "]", l -> l.stream().anyMatch(valueIs));
  }
}


