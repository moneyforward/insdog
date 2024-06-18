package jp.co.moneyforward.autotest.ca_web.tests;

import jp.co.moneyforward.autotest.ca_web.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;

/**
 * This test assumes the account returned by the profile is clean.
 * That is:
 *
 * - it can log in to the SUT with its password
 * - it doesn't have any connected banks.
 *
 * @see ExecutionProfile#userEmail()
 * @see ExecutionProfile#userPassword()
 * @see ExecutionProfile#accountServiceId()
 */
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connectBank", "disconnectBank", "logout"},
        afterEach = {"screenshot"},
        afterAll = {"close"}))
public class BankConnectingTest extends CawebAccessingModel {
}
