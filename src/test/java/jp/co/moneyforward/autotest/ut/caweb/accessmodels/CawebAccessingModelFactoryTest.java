package jp.co.moneyforward.autotest.ut.caweb.accessmodels;

import jp.co.moneyforward.autotest.ca_web.core.CawebExecutionProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class CawebAccessingModelFactoryTest {
  @ParameterizedTest
  @MethodSource("branchNameInstanceSpecifierPairs")
  void givenAtMarkContainingBranchName_whenCreate_thenDomainNameContainsHostnameAfterAtMark(String branchName, String instanceSpecifier) {
    CawebExecutionProfile.Factory factory = new CawebExecutionProfile.Factory();
    
    String domain = factory.create(branchName).domain();
    
    assertStatement(value(domain).toBe().containing(instanceSpecifier));
  }
  
  static Stream<Arguments> branchNameInstanceSpecifierPairs() {
    return Stream.of(
        Arguments.arguments("develop-hello@WORLD", "WORLD"),
        Arguments.arguments("develop-helloWorld", "ebisubook"),
        Arguments.arguments(null, "ebisubook"));
  }
}
