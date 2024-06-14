package jp.co.moneyforward.autotest.ca_web.tests;

import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        beforeAll = {"open"},
        beforeEach = {},
        value = {"login", "connect", "disconnect", "logout"},
        afterEach = {"screenshot"},
        afterAll = {"close"}))
public class BankConnectingTest {
}
