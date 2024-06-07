package jp.co.moneyforward.autotest.ututils;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.io.Writer;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.ActionComposer;
import jp.co.moneyforward.autotest.ut.framework.scene.SceneTest;

import java.util.Collections;

import static java.util.Collections.emptyMap;

public enum ActionUtils {
  ;
  
  public static ActionComposer createActionComposer() {
    return ActionComposer.createActionComposer("IN", "WORK", "OUT", SceneTest.createExecutionEnvironment(), emptyMap());
  }
  
  public static void performAction(Action action) {
    ReportingActionPerformer.create().performAndReport(action, Writer.Std.OUT);
  }
}
