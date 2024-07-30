package jp.co.moneyforward.autotest.ut.utils;

import jp.co.moneyforward.autotest.framework.action.Call;
import jp.co.moneyforward.autotest.framework.action.LeafAct;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class InternalUtilsTest {
  @Test
  void whenChainActs_thenCreatedSceneLooksCorrect() {
    Scene scene = InternalUtils.chainActs("var1", new LeafAct.Func<>((String x) -> x + "a"), new LeafAct.Func<>((String x) -> x + "b"));
    
    assertStatement(value(scene.children().stream().map(Call::outputFieldName).toList())
                        .toBe()
                        .equalTo(List.of("var1", "var1")));
  }
}
