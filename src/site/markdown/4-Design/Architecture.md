# Software Architecture

Following is a diagram that illustrates relationships between software components of **autotest-ca** and of external services.

Note that some interactions in this diagram from **SDET(app)** are done not only by them but also by other roles.

*Logical Integration:*
```mermaid
graph LR
    subgraph "Product" 
        SUT
    end
    subgraph "SDEs"
        SDETapp
        SDETfw
        SDE
    end
    SDE -.->|designs and implements| autotest-tests
    SDE -.->|designs, implements, and tests| SUT
    SDETapp("SDET(App)")
    SDETapp -->|reads spec| SUT
    SDETapp -.->|manually tests| SUT
    SDETapp -.->|writes unit tests of| SUT
    SDETapp -.->|manually invokes| gha
    SDETapp -.->|designs and implements| autotest-tests
    SDETapp -.->|executes locally| autotest-cli
    SDETfw("SDET(Framework)")
    SDETfw -.->|designs and implements| autotest-cli
    SDETfw -.->|designs and implements| autotest-workflows
    SDETfw -.->|designs and implements| autotest-fw
    SDETfw -.->|refactors| autotest-tests
    subgraph "autotest-ca"
        autotest-tests("autoest-ca Tests")
        autotest-fw("autoest-ca Framework")
        autotest-cli("CLI Tools")
        autotest-profile("Profiles")
        autotest-workflows("Workflows")
        subgraph "third-party libraries" 
            JUnit5
            Playwright-Java
        end
    end
    autotest-workflows -.-> |invokes| autotest-cli
    autotest-cli -.->|store execution reports| testRail
    autotest-cli -.->|deploy and publish artifacts| ghPackages
    autotest-cli -.->|publish documents| backstage
    autotest-cli -.->|requests execution| autotest-fw
    autotest-fw -.->|executes| autotest-tests
    autotest-fw --> autotest-profile
    autotest-tests -.->|accesses| SUT
    subgraph "External Services"
        gha("GitHub Actions")
        testRail("TestRail")
        ghPackages("GitHub Packages")
        backstage("Backstage")
    end

    gha -.->|triggers| autotest-cli
```

**CLI Tools** are designed to access external services and implement peripheral tasks around development efforts, such as building executables, publishing documentations, and executing tests.
They have in general entry-points in **Makefile**.
(GitHub) **Workflows** are implemented to hook triggers as wrappers to those **CLI Tools** 
They are not supposed to access external services or implements complex logics by themselves.
Instead, they should invoke **CLI tools** and be kept as light-weight as possible.
Insight behind this is a fact, where workflows of GitHub Actions are hard to test even with helps by `gh` utility command, that GitHub distributes.

**autotest-ca Framework** is a component responsible for executing tests defined as a part of **autotest-ca Tests** component.
The framework provides a **JUnit5** extension, which defines execution flow of tests.

**autotest-ca Profile** is a component that abstracts execution environments and parameters whose values can be different across test runs.

**SDET(App)** are responsible for executing/implementing tests for **SUT** in an automated-fashion as much as possible to meet projects' deadlines.
When necessary, they will execute tests by manual.
While **SDET(Framwork)** are responsible for making/keeping **SDET(App)** works efficient as much as possible.

## Internal

Following is a diagram more focusing on the detail of the static structure of the `autotest-ca`.
It is built as an executable assembly, which has all the necessary dependencies inside one file.

```mermaid
C4Context

Person(sdetFramework, "SDET(framework)")
Rel(sdetFramework, abstractActions, "Creates")
Rel(sdetFramework, builtInActions, "Creates")
Rel(sdetFramework, extension, "Creates")

Person(sdetTests, "SDET(App)")
Rel(sdetTests, testClasses, "Creates")
Rel(sdetTests, junit-platform-launcher, "0-a. Invokes")

Person(ci, "C/I system")
Rel(ci, junit-platform-launcher, "0-b. Invokes")


Rel(junit-platform-launcher, extension, "1. instantiates")
Rel(junit-platform-launcher, testClasses, "2. instantiates")
Rel(extension, testClasses, "3. executes")

Boundary(autotest-ca-assembly, "autotest-ca:assembly", "application") {
    Boundary(b0, "autotest-ca:main", "library") {
        Boundary(coreBoundary, "core", "package") {
            Component(extension, "JUnit 5 Test Extension")
            Component(abstractActions, "Base Action Factories")
            Rel(builtInActions, abstractActions, "implements")
            Component(builtInActions, "Built-in Action Factories")
        }
        Boundary(b2, "tests", "package") {
            Component(testClasses, "Tests")
            Component(customActions, "Custom Action Factories")
            Rel(testClasses, customActions, "Uses")
        }
        Rel(testClasses, builtInActions, "Uses")
    }
    Boundary(oss, "Third-party OSS Libraries") {
        Component(junit-platform-launcher, "junit-platform-launcher")
        Component(playwrightJava, "Playwright Java")
        Rel(customActions, playwrightJava, "dependsOn")
        Rel(builtInActions, playwrightJava, "dependsOn")
    }

}

Boundary(platform, "Platform (OS)") {
  Component(browser, "Browser")
}
Rel(playwrightJava, browser, "accesses")
```

It is assumed that different set of people will work on each package.
`tests` will be developed by "SDET(App)", who are supposed to be assigned to a specific product/project and knowledgeable at its specifications and expertise in software testing.
They will conduct manual tests when necessary to meet project requirements.

The `core` will be developed by "SDET(Framework)", who are knowledgeable both at general software engineering and software testing.

In the current version  (version **1.0.0-SNAPSHOT**), `tests` and `core` will be placed in the same library module, however, when it goes to production, they will be belonging to different modules and different repositories.
This separation will be done as the product gets matured.

## "Profile" Mechanism

This is a diagram that illustrates relationships between components of **autotest-ca** and of external services at runtime.

*Profile Mechanism*

```mermaid
graph TD
    subgraph users [Users]
        SDET
        SDE
    end

    subgraph executors [Autotest Execution Computers]
        subgraph "GitHub Self-hosted Server"
            autotest-ca-ghsh("autotest-ca")
            subgraph "Profiles"
                profile-prod-ghsh("profile-prod")
                profile-stg-ghsh("profile-stg")
                profile-idev-ghsh("profile-idev")
                profile-misc-ghsh("profile-misc")
            end
        end
        subgraph "Laptop"
            autotest-ca-laptop("autotest-ca")
        end
    end

    subgraph sutHost [SUT Hosting Computers]
        subgraph "Production"
            SUT-prod(SUT)
        end
        subgraph "Staging"
            SUT-stg(SUT)
        end
        subgraph "idev"
            SUT-idev(SUT)
        end
        subgraph "Misc"
            SUT-misc(SUT)
        end
    end

    subgraph "External Services"
        gha("GitHub Actions")
        testRail("TestRail")
        ghPackages("GitHub Packages")
        backstage("Backstage")
    end

    SDET -.-> gha
    SDET -.-> autotest-ca-laptop
    autotest-ca-ghsh -.-> profile-prod-ghsh
    autotest-ca-ghsh -.-> profile-stg-ghsh
    autotest-ca-ghsh -.-> profile-idev-ghsh
    autotest-ca-ghsh -.-> profile-misc-ghsh
    profile-prod-ghsh -.-> SUT-prod
    profile-stg-ghsh -.-> SUT-stg
    profile-idev-ghsh -.-> SUT-idev
    profile-misc-ghsh -.-> SUT-misc
    SUT-prod -.-> testRail
    SUT-prod -.-> ghPackages
    SUT-prod -.-> backstage
    gha -.-> autotest-ca-ghsh 
```

**autotest-ca** is designed to be able to run both on laptop computers and GitHub Self-hosted Server transparently.
Its tests are designed to be able to target known execution environments.
Differences between environments are abstracted by a mechanism called **Profile**.
Every component in **autotest-ca** should be designed to be able to run against known execution environments, such as "production", "staging", or "idev", by specifying a profile as a runtime parameter value passed to the CLI, without any single line change in the code.

Thus, every component of **autotest-ca** is meant to be testable.

More detail can be found in [Component Interactions](ComponentInteractions.md)

