# Executing Tests

**autotest-ca** has a CLI with which you can execute the defined tests.
Moreover, it can control how a test is executed in a different way from it is defined at compile time.

This allows programmers to run only tests which they are currently interested in not only for verifying their changes but also for debugging what is going on.
Following is the usage of the CLI tool.

To create the executable, please do `package-without-javadoc` at the project root directory.

**NOTE:** As of today (June/2024), not a few features in this list are yet to be implemented.  

```text
Usage: autotest-cli [-hV] [--execution-profile=<executionProfile>]
                    [--execution-descriptor=<executionDescriptors>]...
                    [-q=<queries>]... [<subcommands>...] [COMMAND]
A command line interface of 'autotest-ca', an automated testing tool for
'caweb'.
      [<subcommands>...]   Subcommands of this CLI.
      --execution-descriptor=<executionDescriptors>
                           Used with 'run' subcommand.
                           An execution descriptor is a JSON and it should look
                             like following:

                           {
                             "beforeAll": ["open"],
                             "beforeEach": [],
                             "tests": ["login", "connectBank",
                             "disconnectBank", "logout"]
                             "afterEach": ["screenshot"],
                             "afterAll": ["close"],
                           }

                           This option can be specified multiple times.
                           If there are more than one, all the combinations
                             between the specified execution descriptors and
                             selected access models (or access models of tests)
                             will be executed.

                           When the descriptor is executed with an access model
                             class, it will specify the scenario to be
                             performed using the model.
                           When it is executed with a test class, it overrides
                             the @AutotestExecution annotation, which is
                             attached to a test class, if an element in a
                             result set of -q, --query options.

                           NOTE: Not yet implemented!

      --execution-profile=<executionProfile>
                           Used with 'run' subcommand.

                           Specifies an execution profile, with which you can
                             override a test's execution time parameters such
                             as: user email, password, etc.

                           NOTE: Not yet implemented!

  -h, --help               Show this help message and exit.
  -q, --query=<queries>    Specifies a query. If multiple options are give,
                             they will be treated as disjunctions.

                           QUERY      ::= QUERY_TERM
                           QUERY_TERM ::= ATTR ':' OP COND
                           ATTR       ::= ('classname'|'tag')
                           OP         ::= ('=' | '~')
                           COND       ::= ('*'|CLASS_NAME|TAG_NAME)
                           CLASS_NAME ::= {Java-wise valid character sequence}
                           TAG_NAME   ::= (Any string)

                           This should be used with run, list-testclasses, and
                             list-tags subcommands.

                           NOTE:
                             '=' (OP): Exact match
                             '~' (OP): Partial match

  -V, --version            Print version information and exit.
Commands:
  list-accessmodels                   Prints all known access models.
                                      An <accessmodel> in the result can be
                                        used in a "accessmodel:<tag>" query
                                        given to -q, --query options.

                                      NOTE: Not yet implemented!

  list-tags                           Prints all known tags.
                                      A <tag> in the result can be used in a
                                        "tag:<tag>" query given to -q, --query=
                                        options.

  list-testclasses                    Prints all known tests.
                                      A <testname> in the result can be used in
                                        a "classname:<testname>" given to -q,
                                        --query options

  run                                 Runs tests matching with any of -q,
                                        --query options.

                                      Even if one test matches with multiple
                                        -q, -query options, it will be executed
                                        only once.

  show-default-execution-descriptors  Show default execution descriptors of
                                        tests matching with any of -q, --query
                                        options.

                                      Even if one test matches with multiple
                                        -q, -query options, it will be shown
                                        only once.

                                      NOTE: Not yet implemented!

  show-default-execution-profile      Show default execution profile.

                                      NOTE: Not yet implemented!
```

## Examples

**Running all the tests:**

```text
java -jar target/autotest-caweb.jar -q 'classname:~.*' run
```

The part `-q 'classname:~.*'` means: query (**-q**, **--query**) where **classname:** matches with regular expression (**~**) `.*`.
If you want to do exact match, you can do `-q 'classname:=fully.qualified.ClassName`.
For partial match, you can do: `-q 'classname:%ClassName'`

Actually, just to run all tests, you can omit the `-q` option in the example because its default is desinged to be `classname:~.*`. 

**List all tags:**

Tag is an annotation attached to tests.

```text
java -jar target/autotest-caweb.jar list-tags
```

This may print:
```text
smoke
bank
```

You can use it to query tests, which match given tags.
With this feature, you can run desired tests only.
This is useful, when you are doing debugging/refactoring the SUT.
Check the `run` subcommand, either.

**Run tests matching tags:**

```text
java -jar target/autotest-caweb.jar -q 'tag:%bank' run
```

To define and attach tags to test classes, check [ProgrammingModel](ProgrammingModel.md).

**List tests:**

This will print all the known tests:

```text
java -jar target/autotest-caweb.jar list-tests
```

And it may print following:

```text
class jp.co.moneyforward.autotest.ca_web.tests.bankaccount.BankConnectingTest
class jp.co.moneyforward.autotest.ca_web.tests.pages.VisitAllMenuItemsTest
```

By specifying queries (`-q`, `--query`), you can identify tests to be executed by `run` subcommand without actually executing them.
That is, 

```text
java -jar target/autotest-caweb.jar -q 'tag:%bank' list-tests
```

will print only:

```text
class jp.co.moneyforward.autotest.ca_web.tests.bankaccount.BankConnectingTest
```

Because `BankConnectingTest` is the only test to which `bank` tag is attached, currently.

**Run tests matching test name:**

The query syntax can take `classname`, not only `tag`, as an attribute with which matching happens.

```text
java -jar target/autotest-caweb.jar -q `classname:%BankConnectingTest`
```



