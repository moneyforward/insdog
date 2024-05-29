package jp.co.moneyforward.autotest.ut.sandbox;

import jp.co.moneyforward.autotest.ut.misc.Play;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.AutotestExecution;
import jp.co.moneyforward.autotest.framework.testengine.AutotestEngine;
import org.junit.jupiter.api.extension.ExtendWith;

import static jp.co.moneyforward.autotest.ut.framework.scene.ActUtils.helloAct;
import static jp.co.moneyforward.autotest.ut.framework.scene.ActUtils.let;

@SuppressWarnings("ClassEscapesDefinedScope")
@ExtendWith(AutotestEngine.class)
public class ActionSandbox2 {
  @AutotestExecution
  public static Play play() {
    return new Play.Builder().addMain(new Scene.Builder()
                                          .add(let("Scott Tiger"))
                                          .add(helloAct())
                                          .build())
                             .build();
  }
}
