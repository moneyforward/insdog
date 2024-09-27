package jp.co.moneyforward.autotest.framework.action;

import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class SceneBuilder {
  List<Call> calls = new ArrayList<>();
  
  public SceneBuilder() {
  
  }
  
  public <T, R> SceneBuilder add(Act<T, R> act) {
    calls.add(actToCall(act));
    return this;
  }
  
  private static <R, T> Call actToCall(Act<T, R> act) {
    return new ActCall<>("", act, "");
  }
  
  private static Call sceneToCall(Scene childScene) {
    return new SceneCall("", childScene);
  }
  
  public SceneBuilder add(Scene childScene) {
    calls.add(sceneToCall(childScene));
    return this;
  }
  
  
  public Scene build() {
    return new Scene() {
      @Override
      public List<Call> children() {
        return List.of();
      }
    };
  }
  
  public static Scene sceneCreatingMethod() {
    return builder().add(createPageAct("Hello"))
                    .add(builder().add(createPageAct("next"))
                                  .build())
                    .build();
  }
  
  private static SceneBuilder builder() {
    return new SceneBuilder();
  }
  
  private static PageAct createPageAct(String Hello) {
    return new PageAct(Hello) {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
      
      }
    };
  }
}
