package jp.co.moneyforward.autotest.ut.framework;

import jp.co.moneyforward.autotest.framework.cli.CliBase;
import picocli.CommandLine.Command;

@Command(
    name = "InsDog-example-CLI", mixinStandardHelpOptions = true,
    version = "0.0",
    description = "An example command line interface of 'insdog', an automated testing tool.")
public class CliExample extends CliBase {
  @Override
  protected String rootPackageName() {
    return this.getClass().getPackage().getName();
  }
}
