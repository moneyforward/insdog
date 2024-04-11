package com.github.dakusui.java8.template;

/**
 * An entry-point class of Java8 Template.
 */
public class Java8App {
  Java8App() {
  }
  
  /**
   * This is an entry-point class of the Java 8 example project.
   *
   * [ditaa]
   * .Ditaa diagram example
   * ----
   * +--------+   +-------+    +-------+
   * |        +---+ ditaa +--> |       |
   * |  Text  |   +-------+    |diagram|
   * |Document|   |!magic!|    |       |
   * |     {d}|   |       |    |       |
   * +---+----+   +-------+    +-------+
   *     :                         ^
   *     |       Lots of work      |
   *     +-------------------------+
   * ----
   *
   * Have fun!
   *
   * @param args Arguments passed through the command line.
   */
  public static void main(String... args) {
    Java8App app = new Java8App();
    for (String i: args)
      System.out.println(app.process(i));
  }
  
  /**
   * A processing method.
   * @param s A string to be processed.
   * @return A processed string.
   */
  public String process(String s) {
    return "processed:" + s;
  }
}
