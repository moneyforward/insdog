package jp.co.moneyforward.autotest.framework.action;

public sealed interface TargetedCall extends Call permits AssertionCall, RetryCall  {
}
