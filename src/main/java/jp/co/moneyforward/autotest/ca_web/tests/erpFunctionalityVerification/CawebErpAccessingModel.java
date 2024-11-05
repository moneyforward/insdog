package jp.co.moneyforward.autotest.ca_web.tests.erpFunctionalityVerification;

import com.microsoft.playwright.Dialog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import jp.co.moneyforward.autotest.actions.web.Navigate;
import jp.co.moneyforward.autotest.actions.web.PageAct;
import jp.co.moneyforward.autotest.actions.web.TableQuery;
import jp.co.moneyforward.autotest.ca_web.accessmodels.CawebAccessingModel;
import jp.co.moneyforward.autotest.framework.action.Scene;
import jp.co.moneyforward.autotest.framework.annotations.DependsOn;
import jp.co.moneyforward.autotest.framework.annotations.Export;
import jp.co.moneyforward.autotest.framework.annotations.Named;
import jp.co.moneyforward.autotest.framework.core.ExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

import static jp.co.moneyforward.autotest.actions.web.TableQuery.Term.term;
import static jp.co.moneyforward.autotest.ca_web.accessmodels.CawebUtils.navigateToMenuItemUnderOfficeSettingItem;

public abstract class CawebErpAccessingModel extends CawebAccessingModel  {
  String officeNameToBeChangedTo() {
    return executionProfile().officeName(this);
  }
  
  @Named
  @DependsOn("login")
  @Export({"page", "officeName"})
  public Scene testInitialize() {
    return new Scene.Builder("page")
        .add(navigateToMenuItemUnderOfficeSettingItem("事業者・年度の管理", "#dropdown-office"))
        .add(changeIfOfficePresent(officeNameToBeChangedTo()))
        .build();
  }
  
  public static PageAct changeIfOfficePresent(final String officeName) {
    return new PageAct("Update Office setting, check a checkbox and select for selection box") {
      @Override
      protected void action(Page page, ExecutionEnvironment executionEnvironment) {
        if (page.locator("#dropdown-office").textContent().contains(officeName)) {
          return;
        }
        
        page.onceDialog(Dialog::accept);
        TableQuery.select("事業者・年度の切替")
                  .from("#js-ca-main-contents > table")
                  .normalizeWith(normalizerFunctionForOfficeTable())
                  .where(term("事業者名", officeName),
                         term("会計年度", "2024年度"))
                  .$()
                  .perform(page)
                  .getFirst()
                  .getByText("切替")
                  .click();
      }
    };
  }
  public static BinaryOperator<List<Locator>> normalizerFunctionForOfficeTable() {
    return (lastCompleteRow, incompleteRow) -> {
      List<Locator> ret = new ArrayList<>(lastCompleteRow.size());
      for (int i = 0; i < lastCompleteRow.size(); i++) {
        int offset = lastCompleteRow.size() - incompleteRow.size() - 1;
        ret.add((i < offset || i == lastCompleteRow.size() - 1) ? lastCompleteRow.get(i)
                                                                : incompleteRow.get(i - (offset)));
      }
      return ret;
    };
  }
}