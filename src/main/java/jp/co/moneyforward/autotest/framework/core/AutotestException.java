package jp.co.moneyforward.autotest.framework.core;

/**
 * An exception for the **autotest** framework.
 */
public class AutotestException extends RuntimeException {
  /**
   * Creates an object of this class.
   * @param message A message for this exception.
   * @param cause A cause of this exception, if any. Otherwise, `null`.
   */
  public AutotestException(String message, Throwable cause) {
    super(message, cause);
  }
}
