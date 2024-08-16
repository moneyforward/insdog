package jp.co.moneyforward.autotest.ut.cli;

import jp.co.moneyforward.autotest.framework.cli.CliUtils;
import jp.co.moneyforward.autotest.ut.cli.testpackage.tags.Hello1TaggedClass;
import jp.co.moneyforward.autotest.ut.cli.testpackage.tags.Hello2TaggedClass;
import jp.co.moneyforward.autotest.ut.cli.testpackage.tags.NoTaggedClass;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.valid8j.fluent.Expectations.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliUtilsTest extends TestBase {
  @Test
  void whenListTags_thenTaggedClassesReturned() {
    List<String> tags = CliUtils.listTags(
                                    this.getClass().getPackageName() + ".testpackage.tags")
                                .stream()
                                .sorted()
                                .toList();
    
    assertStatement(value(tags).toBe()
                               .equalTo(asList("hello1", "hello2")));
  }
  
  @Test
  void givenMatchingAnyTestClassQueryUsingRegex_whenListTestClasses_thenAllTestClassesReturned() {
    List<Class<?>> tags = CliUtils.listTestClasses(
                                      new String[]{"classname:~.*"},
                                      this.getClass().getPackageName() + ".testpackage.tags")
                                  .stream()
                                  .toList();
    
    assertStatement(value(tags).function(HashSet::new)
                               .toBe()
                               .equalTo(Set.of(Hello1TaggedClass.class,
                                               Hello2TaggedClass.class,
                                               NoTaggedClass.class)));
  }
  
  @Test
  void givenMatchingTestClassesButNotAllQueryUsingClassNamePartialMatch_whenListTestClasses_thenOnlyMatchingOnesReturned() {
    List<Class<?>> tags = CliUtils.listTestClasses(
                                      new String[]{"classname:%Hello"},
                                      this.getClass().getPackageName() + ".testpackage.tags")
                                  .stream()
                                  .toList();
    
    assertStatement(value(tags).function(HashSet::new)
                               .toBe()
                               .equalTo(Set.of(Hello1TaggedClass.class,
                                               Hello2TaggedClass.class)));
  }
  
  @Test
  void givenMatchingOnlyOneClassQueryUsingExactMatch_whenListTestClasses_thenOnlyIntendedOneReturned() {
    List<Class<?>> tags = CliUtils.listTestClasses(
                                      new String[]{"classname:=Hello1TaggedClass"},
                                      this.getClass().getPackageName() + ".testpackage.tags")
                                  .stream()
                                  .toList();
    
    assertStatement(value(tags).function(HashSet::new)
                               .toBe()
                               .equalTo(Set.of(Hello1TaggedClass.class)));
  }
  
  
  @Test
  void givenMatchingSomeTestClassesUsingTagValueRegex_whenListTestClasses_thenClassesWithTagsReturned() {
    List<Class<?>> tags = CliUtils.listTestClasses(
                                      new String[]{"tag:~.*"},
                                      this.getClass().getPackageName() + ".testpackage.tags")
                                  .stream()
                                  .toList();
    
    assertStatement(value(tags).function(HashSet::new)
                               .toBe()
                               .equalTo(Set.of(Hello1TaggedClass.class,
                                               Hello2TaggedClass.class)));
  }
  
  @Test
  void givenMatchingNothingUsingTagExact_whenListTestClasses_thenNothingReturned() {
    List<Class<?>> tags = CliUtils.listTestClasses(
                                      new String[]{"tag:=unknownTag"},
                                      this.getClass().getPackageName() + ".testpackage.tags")
                                  .stream()
                                  .toList();
    
    assertStatement(value(tags).toBe().empty());
  }
  
  @Test
  void givenInvalidQuery_thenListTestClasses_thenIllegalArgumentExceptionThrown() {
    assertThrows(IllegalArgumentException.class,
                 () -> {
                   try {
                     CliUtils.listTestClasses(
                                 new String[]{"XYZ:~unknownTag"},
                                 this.getClass().getPackageName() + ".testpackage.tags")
                             .stream()
                             .toList();
                   } catch (IllegalArgumentException e) {
                     assertStatement(value(e.getMessage()).toBe()
                                                          .containing("XYZ:~unknownTag")
                                                          .containing("didn't match"));
                     throw e;
                   }
                 });
    
  }
  
  @Test
  void whenComposeSceneDescriptorPropertyValue() {
    var composedPropertyValue = CliUtils.composeSceneDescriptorPropertyValue(new String[]{"beforeAll=beforeAllStep", "beforeEach=beforeEachStep", "value=test1,test2", "afterEach=afterEachStep", "afterAll=afterAllStep"});
    System.out.println(composedPropertyValue);
    List<String> list = asList(composedPropertyValue.substring("inline:".length())
                                                    .split(";"));
    assertAll(value(composedPropertyValue).toBe().startingWith("inline:"),
              value(list).size().toBe().equalTo(5),
              value(list).toBe().containing("beforeAll=beforeAllStep"),
              value(list).toBe().containing("beforeEach=beforeEachStep"),
              value(list).toBe().containing("value=test1,test2"),
              value(list).toBe().containing("afterEach=afterEachStep"),
              value(list).toBe().containing("afterAll=afterAllStep"));
  }
  
  @Test
  void givenOperatorSkippedQueryForClassname_whenParseQuery_thenUnsupportedOperationException() {
    var predicate = CliUtils.parseQuery("classname:XYZ");
    System.out.println(predicate);
    assertStatement(value(predicate).stringify().toBe().equalTo("simpleName isEqualTo[XYZ]"));
  }
  
  @Test
  void givenOperatorSkippedQueryForTagName_whenParseQuery_thenUnsupportedOperationException() {
    var predicate = CliUtils.parseQuery("tag:XYZ");
    System.out.println(predicate);
    assertStatement(value(predicate).stringify().toBe().equalTo("hasTagMatching[valueEqualTo[XYZ]]"));
  }
  
  @Test
  void givenSubstringOperatorForTagName_whenParseQuery_thenUnsupportedOperationException() {
    var predicate = CliUtils.parseQuery("tag:%XYZ");
    System.out.println(predicate);
    assertStatement(value(predicate).stringify().toBe().equalTo("hasTagMatching[valueContaining[XYZ]]"));
  }
}
