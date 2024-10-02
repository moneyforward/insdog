package jp.co.moneyforward.autotest.ca_web.tests.term;

import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy;
import org.junit.jupiter.api.Tag;

@Tag("smoke")
@AutotestExecution(
    defaultExecution = @AutotestExecution.Spec(
        planExecutionWith = PlanningStrategy.DEPENDENCY_BASED,
        beforeEach = {},
        value = {"login"},
        afterEach = {}))
public class Login extends CawebAccessingModel {
}
