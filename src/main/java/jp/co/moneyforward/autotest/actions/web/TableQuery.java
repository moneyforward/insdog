package jp.co.moneyforward.autotest.actions.web;

import com.microsoft.playwright.*;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
  
  public Locator perform(Page page) {
    String headerLocatorString = String.format("%s thead tr", this.tableName());
    Locator headerRow = page.locator(headerLocatorString);
    Locator headerCells = headerRow.locator("th");
    Map<String, Integer> columnIndices = new HashMap<>();
    for (int i = 0; i < headerCells.count(); i++) {
      String columnName = headerCells.nth(i).textContent();
      columnIndices.put(columnName, i);
    }
    
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
      if (columnsInEachRow.size() == columnIndices.size())
        lastCompleteRow = Optional.of(columnsInEachRow);
      List<Locator> eachRow;
      if (lastCompleteRow.isPresent() && columnsInEachRow.size() < columnIndices.size()) {
        eachRow = this.normalizer().apply(lastCompleteRow.orElseThrow(RuntimeException::new),
                                          columnsInEachRow);
      } else {
        eachRow = columnsInEachRow;
      }
      System.out.println(">" + eachRow.stream().map(e -> e.textContent().trim()).collect(Collectors.joining(",")) + "<");

      boolean matched = true;
      for (Term eachQueryTerm : queryTerms()) {
        String filterTargetColumnValue = eachRow.get(columnIndices.get(eachQueryTerm.columnName()))
                                                .textContent();
        if (filterTargetColumnValue.contains(eachQueryTerm.operand())) {
          matched = false;
          break;
        }
      }
      if (matched)
        matches.add(eachRow);
    }
    if (matches.isEmpty()) {
      throw new NoSuchElementException("No matches found for <" + this.queryTerms() + "> in <" + tableName() + ">");
    }
    if (matches.size() > 1) {
      throw new NoSuchElementException("Too many matches: " + matches);
    }
    return matches.getFirst().get(columnIndices.get(columnName));
  }
  
  private static List<Locator> toColumns(Locator row) {
    return row.locator("td").all();
  }
  
  private static String formatRow(Locator eachRow) {
    return eachRow.locator("td").all().stream().map(e -> e.textContent().trim()).map(s -> "<" + s + ">").collect(Collectors.joining(",", "[", "]"));
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
    private BiFunction<List<Locator>, List<Locator>, List<Locator>> normalizer = (lastFullRow, incompleteRow) -> incompleteRow;
    
    public Builder from(String tableName) {
      this.tableName = requireNonNull(tableName);
      return this;
    }
    
    public Builder where(Term... condition) {
      this.conditions = requireNonNull(condition);
      return this;
    }
    
    public Builder normalizeWith(BiFunction<List<Locator>, List<Locator>, List<Locator>> normalizerFunction) {
      this.normalizer = requireNonNull(normalizerFunction);
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
  
  public static void main(String... args) {
    try (Playwright playwright = Playwright.create()) {
      BrowserType chromium = playwright.chromium();
      try (Browser browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false))) {
        Page page = browser.newPage();
        
        page.navigate("file://" + new File(new File(System.getProperty("user.dir")), "src/test/resources/example.html").getAbsolutePath());
        //#js-ca-main-contents > table > thead
        
        Locator l = TableQuery.select("事業者・年度の切替")
                              .from("body > table")
                              .where(Term.term("事業者名", "abc-154206"))
                              .normalizeWith((lastCompleteRow, incompleteRow) -> {
                                List<Locator> ret = new ArrayList<>(lastCompleteRow.size());
                                for (int i = 0; i < lastCompleteRow.size(); i++) {
                                  int offset = lastCompleteRow.size() - incompleteRow.size();
                                  ret.add(i < offset ? lastCompleteRow.get(i)
                                                     : incompleteRow.get(i - (offset)));
                                }
                                return ret;
                              })
                              .build()
                              .perform(page);
        System.out.println("<<<" + l.getByText("切替") + ">>>");
      }
    }
  }
}
