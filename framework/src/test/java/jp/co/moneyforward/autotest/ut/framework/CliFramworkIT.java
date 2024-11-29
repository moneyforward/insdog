package jp.co.moneyforward.autotest.ut.framework;

import jp.co.moneyforward.autotest.ututils.TestBase;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static com.github.valid8j.fluent.Expectations.assertStatement;
import static com.github.valid8j.fluent.Expectations.value;

class CliFramworkIT extends TestBase {
  @Test
  void testHelp() {
    int exitCode = new CommandLine(new CliExample()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                    .execute("--help");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void testListTags() {
    int exitCode = new CommandLine(new CliExample()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                    .execute("list-tags");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void testListTestClasses() {
    int exitCode = new CommandLine(new CliExample()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                    .execute("list-testclasses");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  @Test
  void testUnknown() {
    int exitCode = new CommandLine(new CliExample()).setExecutionStrategy(new NoExitExecutionStrategy())
                                                    .execute("unknown");
    
    assertStatement(value(exitCode).toBe().equalTo(0));
  }
  
  public static class NoExitExecutionStrategy implements CommandLine.IExecutionStrategy {
    @Override
    public int execute(CommandLine.ParseResult parseResult) {
      return new CommandLine.RunLast() {
        @Override
        public int execute(CommandLine.ParseResult parseResult) throws CommandLine.ExecutionException {
          return 0;//super.execute(parseResult);
        }
      }.execute(parseResult);
    }
  }
}
