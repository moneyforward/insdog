package jp.co.moneyforward.autotest.ut.framework.core;

import jp.co.moneyforward.autotest.framework.core.ExecutionProfile;
import jp.co.moneyforward.autotest.framework.core.ExecutionProfile.CreateWith;

@CreateWith(ExecutionProfileExample.Factory.class)
public interface ExecutionProfileExample extends ExecutionProfile {
  class Factory implements ExecutionProfile.Factory<ExecutionProfileExample> {
    @Override
    public ExecutionProfileExample create(String branchName) {
      return new ExecutionProfileExample() {
      };
    }
  }
}
