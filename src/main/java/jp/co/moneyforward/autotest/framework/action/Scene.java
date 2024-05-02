package jp.co.moneyforward.autotest.framework.action;

import java.util.List;

/**
 * An interface that represents a reusable unit of an action in ngauto-mf's programming model.
 * An instance of this object may contain {@link Act} instances.
 */
public interface Scene extends ActionFactory {
    List<ActionFactory> children();
}
