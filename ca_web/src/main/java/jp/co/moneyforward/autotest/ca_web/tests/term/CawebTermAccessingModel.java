package jp.co.moneyforward.autotest.ca_web.tests.term;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Act;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.function.Function;

public class CawebTermAccessingModel extends CawebAccessingModel {
  @Named
  @DependsOn("login")
  @Export({"page", "officeName"})
  public static Scene createOffice() {
    String officeName = executionProfile().officeName();
    Act<Page, Page>[] acts = new Act[]{
        navigateToTermSelection(),
        createOfficeViaNavis(officeName, executionProfile().userDisplayName())};
    Scene.Builder builder = new Scene.Builder("page");
    builder.add("officeName", new Act.Func<>((Function<Page, String>) page -> officeName), "page");
    for (Act<Page, Page> act : acts) {
      builder.add(act);
    }
    return builder.build();
  }
  
  static PageAct navigateToTermSelection() {
    return new PageAct("Navigate to the term selection page") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        // #js-ca-main-contents > dl:nth-child(7) > dd > a
        officeDropDownLocatorFor(page).click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("事業者・年度の管理")).click();
      }
    };
  }
  
  public static Locator officeDropDownLocatorFor(Page page) {
    return page.locator("#dropdown-office");
  }
  
  static PageAct createOfficeViaNavis(String officeName, String tenantUserDisplayName) {
    return new PageAct("Create an office via Navis") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        String placeholderForCompanyName = "株式会社マネーフォワード\u3000東京都";
        page.onceDialog(Dialog::accept);
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("新しい事業者を作成")).click();
        page.getByPlaceholder(placeholderForCompanyName).click();
        page.getByPlaceholder(placeholderForCompanyName).fill(officeName);
        
        page.locator("div").filter(new Locator.FilterOptions().setHasText("新規事業者作成（無料）事業者区分必須※ 副業や一時所得などで確定申告が必要な方は必ず「個人」をお選びください法人株式会社、合同会社など個人・個人事業主確定申告な")).nth(1).click();
        page.getByPlaceholder(placeholderForCompanyName).click();
        page.getByPlaceholder(placeholderForCompanyName).press("Enter");
        page.getByPlaceholder("都道府県を選択してください").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("北海道")).click();
        page.locator("#tenantUserDisplayName").click();
        page.locator("#tenantUserDisplayName").fill(tenantUserDisplayName);
        page.getByPlaceholder("0312345678").click();
        page.getByPlaceholder("0312345678").fill("000000000");
        page.getByText("~10", new Page.GetByTextOptions().setExact(true)).click();
        page.getByLabel("~10", new Page.GetByLabelOptions().setExact(true)).check();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("上記に同意して登録")).click();
      }
    };
  }
}
