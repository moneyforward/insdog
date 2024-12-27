package jp.co.moneyforward.autotest.lessons;

import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

import java.util.LinkedHashMap;

public class LessonBase implements AutotestRunner {
  @Override
  public ReportingActionPerformer actionPerformer() {
    return new ReportingActionPerformer(InternalUtils.createContext(), new LinkedHashMap<>());
  }
}
