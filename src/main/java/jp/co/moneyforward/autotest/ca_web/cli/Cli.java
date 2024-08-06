package jp.co.moneyforward.autotest.ca_web.cli;

import jp.co.moneyforward.autotest.ca_web.tests.Index;
import jp.co.moneyforward.autotest.framework.cli.CliBase;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * A CLI for **autotest-ca**.
 */
@Command(
    name = "autotest-cli", mixinStandardHelpOptions = true,
    version = "0.0",
    description = "A command line interface of 'autotest-ca', an automated testing tool for 'caweb'.")
public class Cli extends CliBase {
  private static final String ROOT_PACKAGE_NAME = Index.class.getPackageName();
  
  /**
   * Returns a root package under which classes to run by this CLI are searched.
   *
   * @return A root package name on the classpath.
   */
  @Override
  protected String rootPackageName() {
    return ROOT_PACKAGE_NAME;
  }
  
  /**
   * An entry point of this CLI application.
   *
   * @param args Command line arguments.
   * @see CliBase
   */
  public static void main(String... args) {
    int exitCode = new CommandLine(new Cli()).execute(args);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }
}