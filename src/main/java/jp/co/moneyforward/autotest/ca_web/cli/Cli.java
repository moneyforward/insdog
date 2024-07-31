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
  
  @Override
  protected String rootPackageName() {
    return ROOT_PACKAGE_NAME;
  }
  
  public static void main(String... args) {
    int exitCode = 1;
    try {
      exitCode = new CommandLine(new Cli()).execute(args);
    } finally {
      System.exit(exitCode);
    }
  }
}