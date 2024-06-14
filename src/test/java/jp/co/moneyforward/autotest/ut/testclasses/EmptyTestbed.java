package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

@AutotestExecution(defaultExecution = @AutotestExecution.Spec())
public class EmptyTestbed implements AutotestRunner {
  @Override
  public ReportingActionPerformer actionPerformer() {
    return ReportingActionPerformer.create();
  }
}
