package jp.co.moneyforward.autotest.ut.misc;

import jp.co.moneyforward.autotest.framework.action.Scene;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * An interface that represents the top level action object in ngauto-mf's programming model.
 * {@link Scene} instances are performed through an instance of this object at the application level.
 */
public interface Play {
  List<Scene> baseSetUp();
  
  List<Scene> setUp();
  
  List<Scene> mainScenes();
  
  List<Scene> tearDown();
  
  List<Scene> baseTearDown();
  
  class Builder {
    private final List<Scene> baseSetUp;
    private final List<Scene> setUp;
    private final List<Scene> main;
    private final List<Scene> tearDown;
    private final List<Scene> baseTearDown;
    
    public Builder() {
      this.baseSetUp = new LinkedList<>();
      this.setUp = new LinkedList<>();
      this.main = new LinkedList<>();
      this.tearDown = new LinkedList<>();
      this.baseTearDown = new LinkedList<>();
    }
    
    public Builder addBaseSetUp(Scene scene) {
      this.baseSetUp.add(requireNonNull(scene));
      return this;
    }
    public Builder addBaseSetUp(Function<Scene.Builder, Scene.Builder> scene) {
      return this.addBaseSetUp(requireNonNull(scene).apply(new Scene.Builder()).build());
    }

    public Builder addSetUp(Scene scene) {
      this.setUp.add(requireNonNull(scene));
      return this;
    }
    public Builder addSetUp(Function<Scene.Builder, Scene.Builder> scene) {
      return this.addSetUp(requireNonNull(scene).apply(new Scene.Builder()).build());
    }

    public Builder addMain(Scene scene) {
      this.main.add(requireNonNull(scene));
      return this;
    }

    public Builder addMain(Function<Scene.Builder, Scene.Builder> scene) {
      return this.addMain(requireNonNull(scene).apply(new Scene.Builder()).build());
    }

    public Builder addTearDown(Scene scene) {
      this.tearDown.add(requireNonNull(scene));
      return this;
    }
    public Builder addTearDown(Function<Scene.Builder, Scene.Builder> scene) {
      return this.addTearDown(requireNonNull(scene).apply(new Scene.Builder()).build());
    }

    public Builder addBaseTearDown(Scene scene) {
      this.baseTearDown.add(requireNonNull(scene));
      return this;
    }
    public Builder addBaseTearDown(Function<Scene.Builder, Scene.Builder> scene) {
      return this.addBaseTearDown(requireNonNull(scene).apply(new Scene.Builder()).build());
    }

   
    public Play build() {
      return new Play() {
        @Override
        public List<Scene> baseSetUp() {
          return baseSetUp;
        }
        
        @Override
        public List<Scene> setUp() {
          return setUp;
        }
        
        @Override
        public List<Scene> mainScenes() {
          return main;
        }
        
        @Override
        public List<Scene> tearDown() {
          return tearDown;
        }
        
        @Override
        public List<Scene> baseTearDown() {
          return baseTearDown;
        }
      };
    }
  }
}
