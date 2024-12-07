package jp.co.moneyforward.autotest.ututils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.ut.framework.scene.SceneTest;

import java.util.HashMap;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.createContext;

public enum ActionUtils {
  ;
  
  public static ActionComposer createActionComposer() {
    return ActionComposer.createActionComposer(SceneTest.createExecutionEnvironment());
  }
  
  public static void performAction(Action action, Writer writer) {
    performAction(action, createContext(), writer);
  }
  
  public static void performAction(Action action, ReportingActionPerformer reportingActionPerformer, Writer writer) {
    reportingActionPerformer.performAndReport(action, writer);
  }
  public static void performAction(Action action, Context context, Writer writer) {
    performAction(action, createReportingActionPerformer(context), writer);
  }
  
  public static ReportingActionPerformer createReportingActionPerformer() {
    return createReportingActionPerformer(createContext());
  }
  
  public static ReportingActionPerformer createReportingActionPerformer(Context context) {
    return new ReportingActionPerformer(context, new HashMap<>());
  }
  
}
