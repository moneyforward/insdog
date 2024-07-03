package jp.co.moneyforward.autotest.ut.framework.cli;

import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.ca_web.tests.Index;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static jp.co.moneyforward.autotest.framework.cli.ClassFinder.*;

public class ClassFinderTest {
  @Test
  public void testClassFinder() {
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
