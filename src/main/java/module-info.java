module jp.co.moneyforward.autotest {
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
  
  
  exports jp.co.moneyforward.autotest.examples;
  exports jp.co.moneyforward.autotest.framework.action;
  exports jp.co.moneyforward.autotest.framework.core;
  exports jp.co.moneyforward.autotest.framework.annotations;
  exports jp.co.moneyforward.autotest.framework.testengine;
  exports jp.co.moneyforward.autotest.ca_web.actions.gui;
  exports jp.co.moneyforward.autotest.ca_web.core;
}
