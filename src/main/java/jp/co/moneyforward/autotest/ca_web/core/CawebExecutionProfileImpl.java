package jp.co.moneyforward.autotest.ca_web.core;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification.BusinessPlanScenario;
import jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification.FreePlanIndividualPersonal;
import jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification.FreePlanScenario;
import jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification.PersonalPlanScenario;
import jp.co.moneyforward.autotest.actions.web.SendKey;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

public class CawebExecutionProfileImpl implements CawebExecutionProfile {
  
  private static final TimeBasedOneTimePasswordGenerator TIME_BASED_ONE_TIME_PASSWORD_GENERATOR = new TimeBasedOneTimePasswordGenerator();
  
  private final String differentiatingSuffix = InternalUtils.dateToSafeString(InternalUtils.now());
  
  private final String sutDomainName;
  
  public CawebExecutionProfileImpl(String sutDomainName) {
    this.sutDomainName = sutDomainName;
  }
  
  /**
   * The recovery code for this account is below:
   *
   * ```
   * マネーフォワード ID 復元コード:
   * HBIW2362XT3IM
   * PGF3MTNO3STD2
   * ONETHGRTWMGSU
   * B5WAZCIPNH27S
   * MMX343AST3YXE
   * UNFGTATPEUT7C
   * DBUJPBJL5LHBY
   * JDPGVU55EMZHA
   * BXNWMJ3OGNLVG
   * DPOVTBNDR5LZY
   * ```
   * ```
   * SVW6 6DVC 3Z3P SNIN C454 G5HP 5Y5V ZDPA
   * ````
   *
   * @return A user e-mail
   */
  @Override
  public String userEmail() {
    return SendKey.MASK_PREFIX + System.getenv("TEST_USER_EMAIL");
  }
  
  @Override
  public String userPassword() {
    return SendKey.MASK_PREFIX + System.getenv("TEST_USER_PASSWORD");
  }
  
  @Override
  public String totpKeyString() {
    return System.getenv("TEST_TOTP_KEY");
  }
  
  @Override
  public TimeBasedOneTimePasswordGenerator totp() {
    return TIME_BASED_ONE_TIME_PASSWORD_GENERATOR;
  }
  
  @Override
  public String accountServiceId() {
    return SendKey.MASK_PREFIX + System.getenv("TEST_ACCOUNT_SERVICE_ID");
  }
  
  @Override
  public String accountServicePassword() {
    return SendKey.MASK_PREFIX + System.getenv("TEST_ACCOUNT_SERVICE_PASSWORD");
  }
  
  /**
   * Returns if **autotest** should be executed in headless or head-ful.
   * The head-ful is useful for developing and debugging the **autotest** not intended for using it in the C/I environment.
   *
   * @return `true` - headless (default) / `false` - head-ful mode.
   */
  @Override
  public boolean setHeadless() {
    return !InternalUtils.isPresumablyRunningFromIDE();
  }
  
  /**
   * Returns a locale to open a browser for the execution of **autotest**.
   * I.e., the value will be passed to `ContextOptions#setLocale` of **Playwright-java**.
   *
   * Currently, this always returns `ja-JP`.
   *
   * @return The locale, in which the tests should be executed.
   */
  @Override
  public String locale() {
    return "ja-JP";
  }
  
  @Override
  public String domain() {
    return sutDomainName;
  }
  
  @Override
  public String userDisplayName() {
    return "picInAbc-" + differentiatingSuffix;
  }
  
  @Override
  public String officeName(Object model) {
    return switch (model) {
      case BusinessPlanScenario businessPaid -> "abc-154206";
      case FreePlanScenario businessFree -> "abc-173041";
      case PersonalPlanScenario personalPaid -> "PersonalPaid";
      case FreePlanIndividualPersonal personalFree -> "PersonalFree";
      default -> "abc-" + differentiatingSuffix;
    };
  }
}
