package jp.co.moneyforward.autotest.framework.action;

import jp.co.moneyforward.autotest.framework.utils.InternalUtils;

/**
 * **Act** is a unit of action to be performed during a test execution.
 */
public interface Act {
  /**
   * Returns a name of an instance of this interface.
   *
   * @return A name of this instance.
   */
  default String name() {
    return InternalUtils.simpleClassNameOf(this.getClass());
  }
}
