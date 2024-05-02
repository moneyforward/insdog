package jp.co.moneyforward.autotest.framework.action;

import java.util.List;

/**
 * An interface that represents the top level action object in ngauto-mf's programming model.
 * {@link Scene} instances are performed through an instance of this object at the application level.
 */
public interface Play extends ActionFactory {
  List<Scene> scenes();
}
