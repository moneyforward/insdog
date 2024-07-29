package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static com.github.dakusui.valid8j.Requires.requireNonNull;
import static com.github.valid8j.fluent.Expectations.require;
import static com.github.valid8j.fluent.Expectations.value;
import static java.util.Arrays.asList;

public record TableQuery(String tableName, String columnName, List<Term> queryTerms,
                         BiFunction<List<Locator>, List<Locator>, List<Locator>> normalizer) {
  public static TableQuery.Builder select(String columnName) {
    Builder builder = new Builder();
    builder.columnName = columnName;
    return builder;
  }
  
  public List<Locator> perform(Page page) {
    String headerLocatorString = String.format("%s thead tr", this.tableName());
    Locator headerRow = page.locator(headerLocatorString);
    Locator headerCells = headerRow.locator("th");
    Map<String, Integer> columnIndices = composeColumnNameIndices(headerCells);
    
    require(value(columnIndices).function(m -> m.keySet().stream().toList())
                                .asList()
                                .toBe()
                                .containing(columnName()));
    
    Locator rowLocator = page.locator(String.format("%s > tbody > tr", tableName()));
    List<List<Locator>> matches = new ArrayList<>();
    Optional<List<Locator>> lastCompleteRow = Optional.empty();
    for (int i = 0; i < rowLocator.count(); i++) {
      Locator eachRowLocator = rowLocator.nth(i);
      List<Locator> columnsInEachRow = toColumns(eachRowLocator);
      if (isCompleteRow(columnsInEachRow, columnIndices))
        lastCompleteRow = Optional.of(columnsInEachRow);
      List<Locator> eachRow = getLocators(lastCompleteRow, columnsInEachRow, columnIndices);
      
      boolean matched = true;
      for (Term eachQueryTerm : queryTerms()) {
        String filterTargetColumnValue = eachRow.get(columnIndices.get(eachQueryTerm.columnName()))
                                                .textContent();
        if (!filterTargetColumnValue.contains(eachQueryTerm.operand())) {
          matched = false;
          break;
        }
      }
      if (matched)
        matches.add(eachRow);
    }
    return matches.stream()
                  .map(c -> c.get(columnIndices.get(columnName)))
                  .toList();
  }
  
  private static boolean isCompleteRow(List<Locator> columnsInEachRow, Map<String, Integer> columnIndices) {
    return columnsInEachRow.size() == columnIndices.size();
  }
  
  private List<Locator> getLocators(Optional<List<Locator>> lastCompleteRow, List<Locator> columnsInEachRow, Map<String, Integer> columnIndices) {
    List<Locator> eachRow = lastCompleteRow
        .orElse(columnsInEachRow);
    if (lastCompleteRow.isPresent() && columnsInEachRow.size() < columnIndices.size()) {
      eachRow = this.normalizer().apply(lastCompleteRow.orElseThrow(RuntimeException::new),
                                        columnsInEachRow);
    } else {
      eachRow = columnsInEachRow;
    }
    return eachRow;
  }
  
  private static Map<String, Integer> composeColumnNameIndices(Locator headerCells) {
    Map<String, Integer> columnIndices = new HashMap<>();
    for (int i = 0; i < headerCells.count(); i++) {
      String columnName = headerCells.nth(i).textContent();
      columnIndices.put(columnName, i);
    }
    return columnIndices;
  }
  
  private static List<Locator> toColumns(Locator row) {
    return row.locator("td").all();
  }
  
  public record Term(String columnName, String operand) {
    public static Term term(String columnName, String operand) {
      return new Term(columnName, operand);
    }
  }
  
  public static class Builder {
    private String tableName;
    private String columnName;
    private Term[] conditions;
    private BinaryOperator<List<Locator>> normalizer = (lastFullRow, incompleteRow) -> incompleteRow;
    
    public Builder from(String tableName) {
      this.tableName = requireNonNull(tableName);
      return this;
    }
    
    public Builder where(Term... condition) {
      this.conditions = requireNonNull(condition);
      return this;
    }
    
    public Builder normalizeWith(BinaryOperator<List<Locator>> normalizer) {
      this.normalizer = requireNonNull(normalizer);
      return this;
    }
    
    public TableQuery build() {
      return new TableQuery(this.tableName, columnName, asList(conditions), this.normalizer);
    }
    
    /**
     * A shorthand method ob `build()`.
     *
     * @return A built `TableQuery` object.
     */
    public TableQuery $() {
      return build();
    }
  }
  
}
