package jp.co.moneyforward.ngauto.action;

import com.github.dakusui.actionunit.core.Action;

import java.util.List;

/**
 * An interface that represents a reusable unit of an action in ngauto-mf's programming model.
 * An instance of this object may contain {@link Act} instances.
 */
public interface Scene extends ActionFactory {
  List<Act> acts();
  
  interface Composite extends Scene {
    List<Scene> children();
  }
  interface Leaf extends Scene {
  
  }
}
