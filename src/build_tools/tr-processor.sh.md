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

Under a directory specified by `--target`, test case directories are searched.
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


## `testReport.xml`

**Example:**
```xml

<testsuites name="test suites root">
    <testsuite failures="0" errors="0" skipped="1" tests="1" time="3049" name="tests.LoginTests">
    <properties>
      <property name="setting1" value="True"/>
      <property name="setting2" value="value2"/>
    </properties>
    <testcase classname="tests.LoginTests" name="C378035_test_case_1" time="159" id="C378035">
      <skipped type="pytest.skip" message="Please skip">skipped by user</skipped>
    </testcase>
    <testcase classname="tests.LoginTests" name="C378043_test_case_2" time="650" id="002">
      <properties>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-before.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-main.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-after.log"/>
         <property name="testrail_result_field" value="version:1.2"/>
         <property name="testrail_result_field" value="custom_environment:qa02"/>
      </properties>
    </testcase>
    <testcase classname="tests.LoginTests" name="C378044_hi" time="121" id="003">
      <failure type="pytest.failure" message="Fail due to...">failed due to...</failure>
      <properties>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-before.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-main.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-after.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-after.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/screenshot-beforeEach.png"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/screenshot-afterEach.png"/>
        <property name="testrail_result_comment" value="## Finding 1 in login tests:003&#10;Finding 1 in login tests:003, hello hello&#10;howdy&#10;&#10;# test2 new finding&#10;another finding"/>
      </properties>
    </testcase>
    <testcase classname="tests.LoginTests" name="19_レポート_収益内訳" time="121" id="004">
      <failure type="autotest-ca.failure" message="Fail due to unknown funky thing"/>
      <properties>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-before.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-main.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/autotestExecution-after.log"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/screenshot-beforeEach.png"/>
        <property name="testrail_attachment" value="testResult/jp.co.moneyforward.autotest.ca_web.tests.pages.VisitMenuItemsTest/13_会計帳簿_残高試算表_貸借対照表/screenshot-afterEach.png"/>
         <property name="testrail_result_field" value="version:1.2"/>
         <property name="testrail_result_field" value="review_comment:testrail_result_comment"/>
        <property name="testrail_result_comment" value="# Finding 1 in login tests:004"/>
      </properties>
    </testcase>
  </testsuite>
</testsuites>

```

