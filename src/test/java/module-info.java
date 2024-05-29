module jp.co.moneyforward.autotest.test {
  requires jp.co.moneyforward.autotest;
  requires valid8j;
  requires actionunit;
  requires org.junit.jupiter.api;
  requires org.junit.platform.commons;
  requires org.junit.platform.launcher;
  requires org.junit.platform.engine;
  requires org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
  requires org.opentest4j;
  requires playwright;
  requires com.google.gson;
  requires jdk.unsupported;
  
  exports jp.co.moneyforward.autotest.ut.framework.scene;
  exports jp.co.moneyforward.autotest.ututils;
  exports jp.co.moneyforward.autotest.ut.sandbox;
}
