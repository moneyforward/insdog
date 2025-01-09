package jp.co.moneyforward.autotest.lessons;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

import java.util.LinkedHashMap;

import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.createContext;

public class LessonBase implements AutotestRunner {
  final Context context = createContext();
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return new ReportingActionPerformer(context, new LinkedHashMap<>());
  }
}
