package jp.co.moneyforward.autotest.ututils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.ut.framework.scene.SceneTest;

import static java.util.Collections.emptyMap;

public enum ActionUtils {
  ;
  
  public static ActionComposer createActionComposer() {
    return ActionComposer.createActionComposer("IN", "WORK", "OUT", SceneTest.createExecutionEnvironment(), emptyMap());
  }
  
  public static void performAction(Action action) {
    performAction(action, createReportingActionPerformer());
  }
  
  public static ReportingActionPerformer createReportingActionPerformer() {
    return ReportingActionPerformer.create();
  }
  
  public static void performAction(Action action, ReportingActionPerformer reportingActionPerformer) {
    reportingActionPerformer.performAndReport(action, Writer.Std.OUT);
  }
}
