package jp.co.moneyforward.autotest.ut.testclasses;

import com.github.dakusui.actionunit.core.Context;
import com.github.dakusui.actionunit.visitors.ReportingActionPerformer;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.ActCall;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.*;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec;
import jp.co.moneyforward.autotest.framework.core.AutotestRunner;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;

import static jp.co.moneyforward.autotest.framework.testengine.PlanningStrategy.DEPENDENCY_BASED;
import static jp.co.moneyforward.autotest.framework.utils.InternalUtils.createContext;

@AutotestExecution(
    defaultExecution = @Spec(
        value = {"connect", "disconnect"},
        planExecutionWith = DEPENDENCY_BASED
    ))
public class StateEnsuringByFallingBackDependencies implements AutotestRunner {
  final Context context = createContext();
  
  /**
   * Creates a scene object from a given name and acts.
   *
   * @param sceneName A name of the created scene.
   * @param acts      acts to be added to the returned scene.
   * @return A created scene.
   */
  @SafeVarargs
  public static <T> Scene sceneFromActCalls(String sceneName, ActCall<T, T>... acts) {
    Scene.Builder builder = new Scene.Builder().name(sceneName);
    for (ActCall<T, T> eachCall : acts) {
      builder.addCall(eachCall);
    }
    return builder.build();
  }
  
  @Named
  @Export({"window", "browser", "page"})
  @ClosedBy("closeExecutionSession")
  public Scene openExecutionSession() {
    return sceneFromActCalls("openPageSession",
                             call("window", act("openWindow", v -> "WindowObject")),
                             call("browser", act("openBrowser", v -> "BrowserObject")),
                             call("page", act("openPage", v -> "PageObject")));
  }
  
  @Named
  @DependsOn("openExecutionSession")
  public Scene closeExecutionSession() {
    return sceneFromActCalls("closeExecutionSession",
                             call("browser", act("closeBrowser", Objects::requireNonNull)),
                             call("window", act("closeWindow", Objects::requireNonNull)));
  }
  
  @Named
  @Export({"page"})
  @DependsOn("openExecutionSession")
  public Scene toHomeScreen() {
    return sceneFromActCalls("toHome",
                             call("page", act("goToHomeScreenByDirectlyEnteringUrl", v -> Objects.requireNonNull(v))));
  }
  
  @Named
  @Export({"page"})
  @DependsOn("openExecutionSession")
  public Scene loadLoginSession() {
    return sceneFromActCalls("loadLoginSession",
                             call("page", act("loadLoginSessionFromFile", Objects::requireNonNull)));
  }
  
  @Named
  @Export("page")
  @DependsOn("openExecutionSession")
  public Scene saveLoginSession() {
    return sceneFromActCalls("saveLoginSession",
                             call("page", act("saveLoginSessionToFile", Objects::requireNonNull)));
  }
  
  /**
   * Let's not specify "logout" for login.
   *
   * A test for log-in and log-out to be performed as expected should be a separate and independent test class.
   *
   * @return A scene that performs "login"
   */
  @Named
  @Export("page")
  @DependsOn("openExecutionSession")
  public Scene login() {
    return sceneFromActCalls("login",
                             call("page", act("enterUsername", Objects::requireNonNull)),
                             call("page", act("enterPassword", Objects::requireNonNull)),
                             call("page", act("clickLogin", Objects::requireNonNull)),
                             call("page", act("enterTOTP", Objects::requireNonNull)),
                             call("page", act("submit", Objects::requireNonNull)));
  }
  
  @Named
  @Export("page")
  @DependsOn("openExecutionSession")
//  /*
  @PreparedBy({"toHomeScreen"})
  @PreparedBy({"loadLoginSession", "toHomeScreen"})
  @PreparedBy({"login", "saveLoginSession"})
//   */
  public Scene isLoggedIn() {
    return sceneFromActCalls("isLoggedIn", call("page", act("checkIfIamOnHomeScreen", Objects::requireNonNull)));
  }
  
  @Named
  @Export("page")
  @DependsOn("isLoggedIn")
  public Scene connect() {
    return sceneFromActCalls("connect", call("page", act("connectBank", Objects::requireNonNull)));
  }
  
  @Named
  @Export("page")
  @DependsOn("isLoggedIn")
  public Scene disconnect() {
    return sceneFromActCalls("disconnect", call("page", act("disconnectBank")));
  }
  
  @Export("page")
  @Named
  public Scene logout() {
    return sceneFromActCalls("logout", call("page", act("loggingOut")));
  }
  
  @Override
  public ReportingActionPerformer actionPerformer() {
    return new ReportingActionPerformer(context, new LinkedHashMap<>());
  }
  
  private static <T> ActCall<T, T> call(String varName, Act<T, T> act) {
    return new ActCall<>(varName, act, varName);
  }
  
  private static <T> Act<T, T> act(String description) {
    return act(description, emptyFunction(description));
  }
  
  private static <T> Act<T, T> act(String description, Function<T, T> function) {
    return Act.create(description, function);
  }
  
  private static <T> Function<T, T> emptyFunction(String description) {
    return x -> {
      System.out.println(description);
      return x;
    };
  }
}
