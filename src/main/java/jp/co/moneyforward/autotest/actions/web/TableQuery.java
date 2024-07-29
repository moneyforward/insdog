package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static com.github.dakusui.valid8j.Requires.requireNonNull;
import static com.github.valid8j.fluent.Expectations.require;
import static com.github.valid8j.fluent.Expectations.value;
import static java.util.Arrays.asList;

/**
 * A class to query HTML table object as if it were an SQL relation.
 * Note that this class is designed to select only one column.
 *
 * <!--- @formatter:off --->
 * ```java
 * void example() {
 *    try (Playwright playwright = Playwright.create()) {
 *       BrowserType chromium = playwright.chromium();
 *       try (Browser browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false))) {
 *         Page page = browser.newPage();
 *         page.navigate(testTableResourcePath());
 *         //#js-ca-main-contents > table > thead
 *
 *         Locator l = TableQuery.select("事業者・年度の切替")
 *                               .from("body > table")
 *                               .where(term("事業者名", "abc-154206"))
 *                               .normalizeWith(normalizerFunction())
 *                               .build()
 *                               .perform(page)
 *                               .getFirst();
 * }
 * ```
 * <!--- @formatter:on --->
 *
 * **Limitation:**
 *
 * - The target HTML table must have unique headers (`th`) for all columns.
 * - Only "equal" condition is supported.
 * - Only conjunctions are supported.
 *
 * In case you think these need to be improved, contact the development team of *autotest-ca*.
 *
 * @param tableName A locator string to specify a table within a `Page` object.
 * @param columnName A column from which value is project to the result.
 * @param queryTerms Condition terms to select rows in a table.
 * @param normalizer A `BinaryOperator` to normalize an incomplete row.
 *
 * @see TableQuery.Term
 */
public record TableQuery(String tableName, String columnName, List<Term> queryTerms,
                         BiFunction<List<Locator>, List<Locator>, List<Locator>> normalizer) {
  /**
   * A method from which you can start building a query.
   * A `Builder` class object, which holds `columnName` as a column to project to the result will be returned.
   *
   * @param columnName A column to be projected into the result.
   * @return A builder object.
   */
  public static TableQuery.Builder select(String columnName) {
    Builder builder = new Builder();
    builder.columnName = columnName;
    return builder;
  }
  
  /**
   * Performs the query on the given `page`.
   *
   * @param page A page object on which this query will be performed.
   * @return A list of locators, each of whose enclosing row that satisfies the `terms`.
   */
  public List<Locator> perform(Page page) {
    String headerLocatorString = String.format("%s thead tr", this.tableName());
    Locator headerRow = page.locator(headerLocatorString);
    Map<String, Integer> columnIndices = composeColumnNameIndices(headerRow.locator("th"));
    
    require(value(columnIndices).function(m -> m.keySet().stream().toList())
                                .asList()
                                .toBe()
                                .containing(columnName()));
    
    List<List<Locator>> matches = new ArrayList<>();
    Optional<List<Locator>> lastCompleteRow = Optional.empty();
    Locator rowLocator = page.locator(String.format("%s > tbody > tr", tableName()));
    for (int i = 0; i < rowLocator.count(); i++) {
      Locator eachRowLocator = rowLocator.nth(i);
      List<Locator> columnsInEachRow = toColumns(eachRowLocator);
      if (isCompleteRow(columnsInEachRow, columnIndices))
        lastCompleteRow = Optional.of(columnsInEachRow);
      List<Locator> eachRow = lastCompleteRow.filter(r -> !isCompleteRow(columnsInEachRow, columnIndices))
                                             .map(r -> this.normalizer().apply(r, columnsInEachRow))
                                             .orElse(columnsInEachRow);
      
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
  
  
  /**
   * A class that represents a term in a condition to select rows.
   * When a value of the column designated by the `columnName` is equal to `operand`, the term is satisfied.
   *
   * @param columnName A name of a column.
   * @param operand    A value
   */
  public record Term(String columnName, String operand) {
    public static Term term(String columnName, String operand) {
      return new Term(columnName, operand);
    }
  }
  
  /**
   * A builder class for `TableQuery`.
   */
  public static class Builder {
    private String tableName;
    private String columnName;
    private Term[] conditions;
    private BinaryOperator<List<Locator>> normalizer = (lastFullRow, incompleteRow) -> incompleteRow;
    
    /**
     * A table name on which the query will be performed.
     *
     * @param tableName A locator string of the table.
     * @return This object.
     */
    public Builder from(String tableName) {
      this.tableName = requireNonNull(tableName);
      return this;
    }
    
    /**
     * @param terms Conditions with which querying
     * @return This object
     * @see TableQuery.Term
     */
    public Builder where(Term... terms) {
      this.conditions = requireNonNull(terms);
      return this;
    }
    
    /**
     * @param normalizer A function that normalizes incomplete row.
     * @return This object
     */
    public Builder normalizeWith(BinaryOperator<List<Locator>> normalizer) {
      this.normalizer = requireNonNull(normalizer);
      return this;
    }
    
    /**
     * Builds a `TableQuery` object from the current field values held by this object.
     *
     * @return A `TableQuery` object.
     */
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
