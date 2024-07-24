package jp.co.moneyforward.autotest.framework.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A base class of CLI for **autotest-ca**.
 */
public abstract class CliBase implements Callable<Integer> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CliBase.class);
  
  protected abstract String rootPackageName();
  
  @CommandLine.Parameters(index = "0..*", description = "Subcommands of this CLI.")
  private List<String> subcommands;
  
  @CommandLine.Option(
      names = {"-q", "--query"},
      description = """
          Specifies a query. If multiple options are given, they will be treated as disjunctions.
          
          QUERY      ::= QUERY_TERM
          QUERY_TERM ::= ATTR ':' OP COND
          ATTR       ::= ('classname'|'tag')
          OP         ::= ('=' | '~' | '%')
          COND       ::= ('*'|CLASS_NAME|TAG_NAME)
          CLASS_NAME ::= {Java-wise valid character sequence for a class name}
          TAG_NAME   ::= (Any string)
          
          This should be used with run, list-testclasses, and list-tags subcommands.
          
          NOTE:
            '=' (OP): Exact match
            '~' (OP): Regular expression match
            '%' (OP): Partial match
          """,
      defaultValue = "classname:~.*")
  private String[] queries;
  
  /**
   * @see jp.co.moneyforward.autotest.framework.annotations.AutotestExecution.Spec.Loader
   */
  @CommandLine.Option(names = {"--execution-descriptor"},
      description = """
          Used with 'run' subcommand.
          An execution descriptor is a JSON and it should look like following:
          
          - --execution-descriptor=beforeAll=open;value=login,connectBank,disconnectBank,logout;afterEach:screenshot,afterAll:close
          
          This option can be specified multiple times.
          If there are more than one, they are merged by concatenating the elements.
          For instance, you can do:
          
          --execution-descriptor=beforeAll=open
          --execution-descriptor=value=login,connectBank,disconnectBank,logout
          --execution-descriptor=afterEach:screenshot
          --execution-descriptor=afterAll=close
          
          Instead of the example shown above.
          """)
  private String[] executionDescriptors = new String[]{};
  
  @Option(names = {"--execution-profile"},
      description = """
          Used with 'run' subcommand.
          
          Specifies an execution profile, with which you can override a test's execution time parameters such as: user email, password, etc.
          
          NOTE: Not yet implemented!
          """)
  private String executionProfile = "";
  
  
  @Command(name = "run",
      description = {"""
          Runs tests matching with any of -q, --query options.
          
          Even if one test matches with multiple -q, -query options, it will be executed only once.
          """})
  public Integer run() {
    int ret;
    try {
      int numFailed = CliUtils.runTests(this.rootPackageName(), this.queries, this.executionDescriptors);
      ret = numFailed == 0 ? 0
                           : 1;
    } catch (IllegalArgumentException e) {
      ret = 2;
    }
    return ret;
  }
  
  @Command(
      name = "list-testclasses",
      description = """
          Prints all known tests.
          A <testname> in the result can be used in a "classname:<testname>" given to -q, --query options
          """)
  public Integer listTestClasses() {
    int ret;
    try (var out = out(); var err = err()) {
      try {
        CliUtils.listTestClasses(this.queries, this.rootPackageName())
                .forEach(out::println);
        ret = 0;
      } catch (IllegalArgumentException e) {
        err.println(e.getMessage());
        ret = 2;
      }
    }
    return ret;
  }
  
  @Command(name = "list-tags",
      description = """
          Prints all known tags.
          A <tag> in the result can be used in a "tag:<tag>" query given to -q, --query= options.
          """)
  public Integer listTags() {
    int ret;
    try (var out = out()) {
      CliUtils.listTags(this.queries, this.rootPackageName())
              .forEach(out::println);
      ret = 0;
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      ret = 2;
    }
    return ret;
  }
  
  @Command(
      name = "list-accessmodels",
      description = """
          Prints all known access models.
          An <accessmodel> in the result can be used in a "accessmodel:<tag>" query given to -q, --query options.
          
          NOTE: Not yet implemented!
          """)
  public Integer listAccessModels() {
    throw new UnsupportedOperationException();
  }
  
  @Command(
      name = "show-default-execution-descriptors",
      description = {"""
          Show default execution descriptors of tests matching with any of -q, --query options.
          
          Even if one test matches with multiple -q, -query options, it will be shown only once.
          
          NOTE: Not yet implemented!
          """})
  public Integer showDefaultExecutionDescriptors() {
    throw new UnsupportedOperationException();
  }
  
  @Command(
      name = "show-default-execution-profile",
      description = {"""
          Show default execution profile.
          
          NOTE: Not yet implemented!
          """})
  public Integer showExecutionProfile() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Integer call() throws Exception { // your business logic goes here...
    try (var err = err()) {
      err.println("You didn't specify known subcommands, try -h, --help option: " + this.subcommands);
      return 2;
    }
  }
  
  private static PrintStream out() {
    return System.out;
  }
  
  private static PrintStream err() {
    return System.err;
  }
}
