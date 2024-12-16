package jp.co.moneyforward.autotest.ut.cli.impl;

import jp.co.moneyforward.autotest.framework.cli.CliBase;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * A CLI for **insdog**.
 */
@Command(
    name = "autotest-cli", mixinStandardHelpOptions = true,
    version = "0.0",
    description = "A command line interface of 'insdog', an automated testing tool for 'caweb'.")
public class CliImpl extends CliBase {
  private static final String ROOT_PACKAGE_NAME = "jp.co.moneyforward.autotest.ut.testclasses";
  
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
    int exitCode = new CommandLine(new CliImpl()).execute(args);
    if (exitCode != 0) {
      throw new RuntimeException(Integer.toString(exitCode));
    }
  }
}