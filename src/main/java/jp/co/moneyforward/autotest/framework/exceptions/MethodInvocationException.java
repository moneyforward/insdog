package jp.co.moneyforward.autotest.framework.exceptions;

public class MethodInvocationException extends RuntimeException {
  public MethodInvocationException(String message, Throwable e) {
    super(message, e);
  }
}
