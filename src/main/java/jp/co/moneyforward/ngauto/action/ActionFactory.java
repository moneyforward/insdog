package jp.co.moneyforward.ngauto.action;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.ngauto.utils.ObjectUtils;

public interface ActionFactory {
  Action create(ObjectUtils.Element... args);
}
