package jp.co.moneyforward.autotest.ca_web;

public class ExecutionProfile {
  public String homeUrl() {
    return "https://accounting-stg1.ebisubook.com/";
  }
  
  public String userEmail() {
    return "ukai.hiroshi+autotest1@moneyforward.co.jp";
  }
  
  public String userPassword() {
    return "!QAZ@WSX";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_id1`.
   *
   * @return An ID for the "account service".
   */
  public String accountServiceId() {
    return "WgeiXfUgHsPn90t5kQtS";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_pw1`.
   *
   * @return A password for the "account service".
   */
  public String accountServicePassword() {
    return "eQCZmxlS1DlmB8Moe710";
  }
}
