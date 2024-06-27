package jp.co.moneyforward.autotest.framework.action;

public interface ActionFactory {
  default String name() {
    return this.getClass().getSimpleName();
  }
}
