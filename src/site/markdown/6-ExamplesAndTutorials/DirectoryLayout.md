# Directory Layout

## Overview

Following is the overview of the **autotest-ca** package.

```
{project directory}
  +-src
    +-build_tools                         tools used for building this project.
    +-main
    | +-java                              Java source files.
    | | `-jp/co/moneyforward/autotest
    | |   +-actions.web                   Basic classes to support testing a web applications are stored.
    | |   +-ca_web                        "ca_web" specific classes are stored.
    | |   +-examples                      Examples to understanding how to program tests using `autotest-ca` are stored.
    | |   `-framework                     SUT-neutral framework codes are stored.
    | +-javadoc                           files used for generating JavaDoc.
    | `-resources                         resources bundled with the executable.
    +-site
    +-test
```

## `jp.co.moneyforward.autotest.ca_web` package

Let's dig in to the **ca_web** (`jp.co.moneyforward.autotest.ca_web`) package, where most of the works happen.

```
{project directory}
  +-src
    +-main
      +-java                          
        `-jp/co/moneyforward/autotest
          +-ca_web                        "ca_web" specific classes are stored.
            +-accessmodels                Access model classes, currently `CaWebAccessModel`, only
            +-cli                         A package for the CLI class.
            +-core                        Core classes for the ca_web applications spec, such as profiles.
            +-tests
              +-bankaccount               "Connecting Banks" tests and related classes
              +-pages                     "Visiting All Menu Items" tests and related classes
              `-(others)                  Test writers can create new packages under `tests` by their decisions.
```

Under each subpackage of `tests`, test writers can create their tests and test packages so that they are organized cleanly and understandable. 