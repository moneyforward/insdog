package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.framework.utils.ObjectUtils;

public interface ActionFactory {
  Action create(ObjectUtils.Element... args);
}
