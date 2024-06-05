package jp.co.moneyforward.autotest.ut.testclasses;

import jp.co.moneyforward.autotest.ca_web.core.ExecutionEnvironmentForCa;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        executionEnvironmentFactory = ExecutionEnvironmentForCa.ExecutionEnvironmentFactory.class))
public class EmptyTestbed implements AutotestRunner {
}
