# Components

Following is a diagram that illustrates static structure of the `autotest-ca` application.
It is built as an executable assembly, which has all the necessary dependencies inside one file.

```mermaid
C4Context

Person(tester, "Tester")
Rel(tester, junit-platform-launcher, "0-a. Invokes")

Person(ci, "C/I system")
Rel(ci, junit-platform-launcher, "0-b. Invokes")


Rel(junit-platform-launcher, extension, "1. instantiates")
Rel(junit-platform-launcher, testClasses, "2. instantiates")
Rel(extension, testClasses, "3. executes")

Boundary(autotest-ca-assembly, "autotest-ca:assembly", "application") {
    Boundary(junit5, "junit5", "library") {
        Component(junit-platform-launcher, "junit-platform-launcher")
    }
    Boundary(b0, "autotest-ca:main", "library") {
        Boundary(b2, "tests", "package") {
            Component(testClasses, "Tests")
            Component(customActions, "Custom Action Factories")
            Rel(testClasses, customActions, "Uses")
        }
        Boundary(coreBoundary, "core", "package") {
            Component(extension, "JUnit 5 Test Extension")
            Component(abstractActions, "Base Action Factories")
            Rel(builtInActions, abstractActions, "implements")
            Component(builtInActions, "Built-in Action Factories")
        }
        Rel(testClasses, builtInActions, "Uses")
    }
}

Person(sdetTests, "SDET-tests")
Rel(sdetTests, testClasses, "Creates")

Person(sdetFramework, "SDET-framework")
Rel(sdetFramework, abstractActions, "Creates")
Rel(sdetFramework, builtInActions, "Creates")
Rel(sdetFramework, extension, "Creates")

```

It is assumed that different set of people will work on each package.
`tests` will be developed by "SDET-tests", who are supposed to be assigned to a specific product/project and knowledgeable at its specifications and expertise in software testing.
the `core` will be developed by "SDET-framework", who are knowledgeable at general software engineering and software testing.

In the PoC, `tests` and `core` will be placed in the same library module, however, when it goes to production, they will be belonging to different modules and different repositories.
This separation will be done as the product gets matured.
