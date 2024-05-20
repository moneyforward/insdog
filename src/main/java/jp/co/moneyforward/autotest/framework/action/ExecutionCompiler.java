package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.List;

import static com.github.dakusui.actionunit.core.ActionSupport.sequential;

public interface ExecutionCompiler {
  Execution compile(Play play, ExecutionEnvironment executionEnvironment);
  
  class Default implements ExecutionCompiler {
    @Override
    public Execution compile(Play play, ExecutionEnvironment executionEnvironment) {
      return new Execution() {
        @Override
        public Action beforeAll() {
          return sequential(play.baseSetUp()
                                .stream()
                                .map(each -> each.toAction("TODO", executionEnvironment, "TODO"))
                                .toList());
        }
        
        @Override
        public Action beforeEach() {
          return sequential(play.setUp()
                                .stream()
                                .map(each -> each.toAction("", executionEnvironment, ""))
                                .toList());
        }
        
        @Override
        public List<Action> main() {
          return play.mainScenes()
                     .stream()
                     .map(each -> each.toAction("", executionEnvironment, ""))
                     .toList();
        }
        
        @Override
        public Action afterEach() {
          return sequential(play.tearDown()
                                .stream()
                                .map(each -> each.toAction("", executionEnvironment, ""))
                                .toList());
        }
        
        @Override
        public Action afterAll() {
          return sequential(play.baseTearDown()
                                .stream()
                                .map(each -> each.toAction("", executionEnvironment, ""))
                                .toList());
        }
      };
    }
  }
}
