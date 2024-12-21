# Directory Layout

## Overview

Following is the overview of the **InsDog** package.

```
{project directory}
  +-src
    +-build_tools                         tools used for building this project.
    +-main
    | +-java                              Java source files.
    | | `-jp/co/moneyforward/autotest
    | |   +-actions.web                   Basic classes to support testing a web applications are stored.
    | |   `-framework                     SUT-neutral framework codes are stored.
    | +-javadoc                           files used for generating JavaDoc.
    | `-resources                         resources bundled with the executable.
    +-site
    +-test
```

Currently, the package `actions.web` exists as a part of this module.
It is the only one that depends on the **Playwright-Java** component.
In future when we introduce another dependency on external client level modules, we will make it an independent submodule together with the `framework` and other clients.
