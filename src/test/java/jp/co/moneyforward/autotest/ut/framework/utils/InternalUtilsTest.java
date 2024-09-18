package jp.co.moneyforward.autotest.ut.framework.utils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.ActionSupport;
import com.github.valid8j.pcond.forms.Printables;
import jp.co.moneyforward.autotest.framework.action.Call;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.core.AutotestException;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.valid8j.fluent.Expectations.*;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternalUtilsTest extends TestBase {
  
  @Test
  void givenEmptyDirectory_whenCurrentBranchName_thenEmpty() throws IOException {
    Path dir = Path.of(".work/testdirs");
    //noinspection ResultOfMethodCallIgnored
    dir.toFile().mkdirs();
    var given = Files.createTempDirectory(dir, this.getClass().getSimpleName()).toFile();
    given.deleteOnExit();
    
    var out = InternalUtils.currentBranchNameFor(given);
    
    System.out.println(out);
    assertAll(value(out).toBe().predicate(Optional::isEmpty));
  }
  
  @Test
  void givenCurrentDirectory_whenCurrentBranchName_thenNonEmpty() {
    var out = InternalUtils.currentBranchName();
    
    System.out.println(out);
    assertAll(
        value(out).toBe().predicate(Optional::isPresent),
        value(out).function(Optional::get).asString().toBe().notEmpty());
  }
  
  @Test
  void givenValidDateString_whenDate_thenParsed() {
    var out = InternalUtils.date("Jul/04/2024");
    assertStatement(value(out).function(Date::toString)
                              .asString()
                              .toBe()
                              .notEmpty());
  }
  
  @Test
  void givenInvalidDateString_whenDate_thenExceptionThrown() {
    try {
      InternalUtils.date("Xyz/04/2024");
    } catch (AutotestException e) {
      assertStatement(value(e).getMessage()
                              .toBe()
                              .notEmpty()
                              .containingSubstrings("Xyz/04/2024"));
      
    }
  }
  
  @Test
  void givenPastDate_whenDateAfter_thenPredicateReturnsTrueForCurrentDate() {
    long base = System.currentTimeMillis();
    var past = new Date(base - 123);
    var now = new Date(base);
    var future = new Date(base + 123);
    
    var out = InternalUtils.dateAfter(now);
    
    assertAll(
        value(out.test(past)).then().falseValue(),
        value(out.test(future)).then().trueValue());
  }
  
  @Test
  void whenNow_thenNonNull() {
    var out = InternalUtils.now();
    assertStatement(value(out).toBe().notNull());
  }
  
  @Test
  void givenNone_whenConcatStreams_thenEmpty() {
    var out = InternalUtils.concat();
    
    assertStatement(value(out)
                        .function(toList())
                        .asListOf(Object.class).toBe().empty());
  }
  
  @Test
  void givenOneStream_whenConcatStreams_thenItself() {
    var out = InternalUtils.concat(Stream.of("A", "B", "C"));
    
    assertStatement(value(out)
                        .function(toList())
                        .asListOf(Object.class)
                        .toBe()
                        .equalTo(List.of("A", "B", "C")));
  }
  
  @Test
  void givenTwoStreams_whenConcatStreams_thenAppended() {
    var out = InternalUtils.concat(Stream.of("A", "B"), Stream.of("C", "D"));
    
    assertStatement(value(out)
                        .function(toList())
                        .asListOf(Object.class)
                        .toBe()
                        .equalTo(List.of("A", "B", "C", "D")));
  }
  
  @Test
  void givenPlainObject_whenIsToStringOverridden_thenFalse() {
    var given = new Object() {
    };
    
    var out = InternalUtils.isToStringOverridden(given);
    
    assertStatement(value(out).toBe().falseValue());
  }
  
  @Test
  void givenToStringOverridingObject_whenIsToStringOverridden_thenFalse() {
    var given = new Object() {
      public String toString() {
        return "hello:" + super.toString();
      }
    };
    
    var out = InternalUtils.isToStringOverridden(given);
    
    assertStatement(value(out).toBe().trueValue());
  }
  
  @Test
  void givenCheckedException_whenWrap_thenWrapped() {
    var given = new IOException("io exception");
    try {
      throw InternalUtils.wrap(given);
    } catch (Exception out) {
      assertAll(value(out).getCause().toBe().instanceOf(IOException.class),
                value(out).toBe().instanceOf(AutotestException.class),
                value(out).getMessage().toBe().containing("io exception"));
    }
  }
  
  @Test
  void givenRuntimeException_whenWrap_thenItself() {
    var given = new NullPointerException("runtime exception");
    try {
      throw InternalUtils.wrap(given);
    } catch (RuntimeException out) {
      assertAll(value(out).toBe().instanceOf(RuntimeException.class),
                value(out).getMessage().toBe().containing("runtime exception"));
    }
  }
  
  @Test
  void givenError_whenWrap_thenItself() {
    var given = new Error("error");
    try {
      throw InternalUtils.wrap(given);
    } catch (Error out) {
      assertAll(value(out).toBe().instanceOf(Error.class),
                value(out).getMessage().toBe().containing("error"));
    }
  }
  
  @Test
  void whenChainActs_thenCreatedSceneLooksCorrect() {
    Scene scene = InternalUtils.chainActs("var1", new Act.Func<>((String x) -> x + "a"), new Act.Func<>((String x) -> x + "b"));
    
    assertStatement(value(scene.children().stream().map(Call::outputFieldName).toList())
                        .toBe()
                        .equalTo(List.of("var1", "var1")));
  }
  
  @Test
  void whenIsPresumablyRunningFromIde_thenFinishesWithoutException() {
    boolean value = InternalUtils.isPresumablyRunningFromIDE();
    
    assertStatement(value(value).toBe().instanceOf(Boolean.class));
  }
  
  @Test
  void givenNonGitProjectDirectory_whenCurrentBranchNameForIsCalled_thenIoExceptionThrown() throws IOException {
    File projectDir = File.createTempFile("dummy", "dir");
    require(value(projectDir.delete()).toBe().trueValue());
    File gitDir = new File(projectDir, ".git");
    require(value(gitDir.mkdirs()).toBe().trueValue());
    gitDir.deleteOnExit();
    projectDir.deleteOnExit();
    assertThrows(IOException.class, () -> {
      try {
        System.out.println(InternalUtils.currentBranchNameFor(projectDir));
      } catch (RuntimeException out) {
        throw out.getCause();
      }
    });
  }
  
  @Test
  void givenNop_whenFlattenIfSequential_thenReturnedOriginalAction() {
    Action nop = ActionSupport.nop();
    
    var returned = InternalUtils.flattenIfSequential(nop).collect(toSet()).iterator().next();
    
    assertStatement(value(returned).toBe().equalTo(nop));
  }
  
  @Test
  void givenParallel_whenFlattenIfSequential_thenReturnedOriginalAction() {
    Action parallel = ActionSupport.parallel(ActionSupport.nop(), ActionSupport.nop());
    
    var returned = InternalUtils.flattenIfSequential(parallel).collect(toSet()).iterator().next();
    
    assertStatement(value(returned).toBe().equalTo(parallel));
  }
  
  @Test
  void givenStringLongerThan120_whenShortenString_thenShortenedStringIsReturned() {
    var s = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
    
    var actual = InternalUtils.shorten(s);
    
    assertStatement(value(actual).length().toBe().equalTo(120));
  }
  
  @Test
  void givenCrContainingString_whenShortenString_thenShortenedStringIsReturned() {
    var s = "1234567890\r1234567890";
    
    var actual = InternalUtils.shorten(s);
    assertStatement(value(actual).toBe().equalTo("1234567890"));
  }
  
  @Test
  void givenStringLongerThan120ContainingCrAfterChar120_whenShortenString_thenShortenedStringIsReturned() {
    var s = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345\rABCD";
    
    var actual = InternalUtils.shorten(s);
    System.out.println(actual);
    
    assertStatement(value(actual).length().toBe().equalTo(120));
  }

  private static <T> Function<Stream<T>, List<T>> toList() {
    return Printables.function("toList", Stream::toList);
  }
  
}
