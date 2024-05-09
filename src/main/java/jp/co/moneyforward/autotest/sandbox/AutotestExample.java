package jp.co.moneyforward.autotest.sandbox;

import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Play;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.ActionTest;

import java.util.List;

import static com.github.valid8j.fluent.Expectations.value;


@ActionTest
public class AutotestExample {
  @ActionTest
  public void example() {
    new Play.Builder()
        .addMain(new Scene.Builder()
                     .add("login", (Act.ForSupplier<List<String>>) x -> List.of("Hello"), x -> value(x).isEmpty())
                     .build())
        .build();
  }
}
