# PoC Requirements Draft

The PoC must not to be fully integrated into the release flow.
Instead, it should be built as such that it can be easily executed at will.

## Must

- Tests can be executed correctly
- Stored in a separate repository
- Executable via CI
- Testing available on STG environment
- Support various test cases. (copy from 駄犬くん)
- Defined Testcase-management
- Written in Java

## Enhancement

- Executable also via Event Handler (trigger w/ commit_hash)
- Check in Playwright for correct commit_hash deployment before testing

## Nice to have

- Jenkins integration (Have Jenkins deploy trigger the Event Handler)
- Execute via Github Action
- Async execution

## Out of Scope

- Other Environments than STG
  - This is out of scope for now, but has to be done later.
  - For idev, cooperation with the SRE team is likely necessary
- Comprehensive Test Suite
  - Creating a Test Suite with the appropriate amount of tests will take a lot of time.
  - This project's focus is on the technical-side and the aim is to proof the capabilities.

## References

- [Playwright PoC Requirements](https://moneyforward.kibe.la/notes/292471)
- [asana: Automatic E2E Testing](https://app.asana.com/0/1206402209253009/1206402209253009)