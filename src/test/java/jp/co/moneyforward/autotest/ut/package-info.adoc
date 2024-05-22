A package that holds (unit/component) test classes of `autotest-ca`.
All the test classes under here extend `TestBase` class to control `stdout`/`stderr` depending on their execution contexts.
That is, we want to see output to see output `stdout`/`stderr`, when we run a test class from IDE, while we do not when we run it through a build tool (`mvn` in our case).

