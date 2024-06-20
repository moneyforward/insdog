package jp.co.moneyforward.autotest.ututils;

import com.github.valid8j.classic.TestAssertions;
import com.github.valid8j.pcond.forms.Predicates;
import jp.co.moneyforward.autotest.framework.utils.Transform;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

import java.util.*;
import java.util.function.Predicate;

import static com.github.valid8j.pcond.forms.Predicates.equalTo;
import static com.github.valid8j.pcond.forms.Printables.function;

public class TestResultValidatorExtension implements TestExecutionListener {
  public record ExpectationEntry(Predicate<TestIdentifier> identifierPredicate,
                                 Predicate<TestExecutionResult> testExecutionResultPredicate) {
    public static class Builder {
      final Predicate<TestIdentifier> testIdentifierPredicate;
      
      public Builder(Predicate<TestIdentifier> testIdentifierPredicate) {
        this.testIdentifierPredicate = testIdentifierPredicate;
      }
      
      public ExpectationEntry shouldBeSuccessful() {
        return shouldBe(Status.SUCCESSFUL);
      }
      
      public ExpectationEntry shouldBeFailed() {
        return shouldBe(Status.FAILED);
      }
      
      public ExpectationEntry shouldBe(Status status) {
        return new ExpectationEntry(this.testIdentifierPredicate,
                                    Transform.$(function("status", TestExecutionResult::getStatus))
                                             .check(equalTo(status)));
      }
    }
  }
  
  public static ExpectationEntry.Builder forTestMatching(String regex) {
    return new ExpectationEntry.Builder(Transform.$(function("displayName", TestIdentifier::getDisplayName))
                                                 .check(Predicates.matchesRegex(regex)));
  }
  
  private final List<ExpectationEntry> expectedResults = new LinkedList<>();
  
  public void addExpectation(ExpectationEntry expectationEntry) {
    this.expectedResults.add(expectationEntry);
  }
  
  
  @Override
  public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    if (!testIdentifier.isTest())
      return;
    Predicate<TestExecutionResult> p = expectationFor(testIdentifier).orElseThrow(() -> new NoSuchElementException(Objects.toString(testIdentifier)));
    TestAssertions.assertThat(testExecutionResult, p);
  }
  
  
  private Optional<Predicate<TestExecutionResult>> expectationFor(TestIdentifier testIdentifier) {
    return expectedResults.stream()
                          .filter(p -> p.identifierPredicate().test(testIdentifier))
                          .map(ExpectationEntry::testExecutionResultPredicate)
                          .findFirst();
  }
}