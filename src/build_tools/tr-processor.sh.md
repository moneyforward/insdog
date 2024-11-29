## Directory Structure

```
testResult/
  {testSuiteName}/
    {testCaseName}/
      RESULT
    {testCaseName}/
      RESULT
  {testSuiteName}/
    {testCaseName}/
      RESULT
```

Under a directory specified by the first argument, test case directories are searched.
A directory which has a file named `RESULT` is considered a test case directory.

Behaviors are unspecified when:

- A test case directory has child test case directories.
- A test suite directory has `RESULT` file.


## `RESULT` file specification

**Example:**
```
TYPE: TEST
RESULT: PASSED
TIME: 999
```

- **TYPE:**
  - When the value is `TEST`, it means the enclosing directory is storing files for a test case. 
  If the value is `CONTAINER`, it means the enclosing directory is storing files for a test suite container, which corresponds a test class, not a test method.
  Typically, test execution time logs produced during `beforeAll` and `afterAll` will be stored.
  - Possible values: `TEST`, `CONTAINER`

## Attachments

`tr-processor.sh` generates test report xml, which indicates to upload files which end with `.png` and `.log`.


## Generated Report

Generated XML looks like following.

```xml

<testsuites name="unnamed">
    <testsuite failures="0" errors="0" skipped="0" tests="0" time="0" name="autotest.log">
    <testcase classname="jp.co.moneyforward.autotest.ca_web.tests.journal_creation.JournalRemoval"
              name="JournalRemoval.JournalRemoval" time="4" id="1">
        <properties>
            <property name="testrail_attachment"
                      value="./target/testResult//jp.co.moneyforward.autotest.ca_web.tests.journal_creation.JournalRemoval/JournalRemoval/autotestExecution-beforeAll.log"/>
            <property name="testrail_attachment"
                      value="./target/testResult//jp.co.moneyforward.autotest.ca_web.tests.journal_creation.JournalRemoval/JournalRemoval/autotestExecution-afterAll.log"/>
        </properties>
    </testcase>
</testsuite>
<testsuite failures="1" errors="0" skipped="0" tests="0" time=""
           name="jp.co.moneyforward.autotest.ca_web.tests.journal_creation.SimpleJournalCreation">
<testcase classname="jp.co.moneyforward.autotest.ca_web.tests.journal_creation.SimpleJournalCreation"
          name="SimpleJournalCreation.SimpleJournalCreation" time="33" id="0">
    <failure type="FAILED" message="failed">
        # Log Files in `SimpleJournalCreation.SimpleJournalCreation`

        ## `autotestExecution-beforeAll.log`

        Log Entries:
        [INFO ] [2024/09/11 10:21:12.548] [main] - Running tests in:
        jp.co.moneyforward.autotest.ca_web.tests.journal_creation.SimpleJournalCreation
        [INFO ] [2024/09/11 10:21:12.548] [main] - ----
        [INFO ] [2024/09/11 10:21:12.548] [main] - Execution plan is as follows:
        [INFO ] [2024/09/11 10:21:12.548] [main] - - beforeAll: [open, login]
        [INFO ] [2024/09/11 10:21:12.548] [main] - - beforeEach: [screenshot]
        [INFO ] [2024/09/11 10:21:12.548] [main] - - value: [clickEasyInputUnderManualEntry, thenClickedItemIsVisible,
        enterJournalRecordWithSimpleInput, thenJournalRecordUpdated, deleteJournalRecord, thenJournalRecordDeleted]
        [INFO ] [2024/09/11 10:21:12.548] [main] - - afterEach: [screenshot]
        [INFO ] [2024/09/11 10:21:12.548] [main] - - afterAll: []
        [INFO ] [2024/09/11 10:21:12.548] [main] - ----
        [INFO ] [2024
        ----
    </failure>
</testcase>
</testsuite>
```