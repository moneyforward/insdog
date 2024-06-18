package jp.co.moneyforward.autotest.ca_web.core;

public class ExecutionProfile {
  public String homeUrl() {
    return String.format("https://%s/", domain());
  }
  
  public String userEmail() {
    return "ukai.hiroshi+autotest1@moneyforward.co.jp";
  }
  
  public String userPassword() {
    return "MASK!!QAZ@WSX";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_id1`.
   *
   * @return An ID for the "account service".
   */
  public String accountServiceId() {
    return "MASK!WgeiXfUgHsPn90t5kQtS";
  }
  
  /**
   * An item stored in `action_files/scenarios/ca/account_registration/data_store/ca_account_registration_data_store_members.csv`
   * as `account_service_form_pw1`.
   *
   * @return A password for the "account service".
   */
  public String accountServicePassword() {
    return "MASK!eQCZmxlS1DlmB8Moe710";
  }
  
  /*
  # comment1,comment2,comment3,*action,*what,attribute,matcher,value
#,直叩き用URL定義,,,,,,
,,,store,ca_accounts_service_list_url,,,https://#{domain}/accounts/service_list
,,,store,ca_accounts_group_url,,,https://#{domain}/accounts/group
,,,store,ca_accounts_url,,,https://#{domain}/accounts

   */
  
  public String accountsUrl() {
    return String.format("https://%s/accounts", domain());
  }
  
  private String domain() {
    return "accounting-stg1.ebisubook.com";
  }
}
