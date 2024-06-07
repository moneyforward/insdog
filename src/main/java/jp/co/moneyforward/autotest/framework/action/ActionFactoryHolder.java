package jp.co.moneyforward.autotest.framework.action;

public interface ActionFactoryHolder<A extends ActionFactory<T, R>, T, R> {
  A get();
  
  String inputFieldName();
  
  String outputFieldName();
  
  static <A extends ActionFactory<T, R>, T, R> ActionFactoryHolder<A, T, R> create(String inputFieldName, String outputFieldName, A actionFactory) {
    return new ActionFactoryHolder<>() {
      @Override
      public A get() {
        return actionFactory;
      }
      
      @Override
      public String inputFieldName() {
        return inputFieldName;
      }
      
      @Override
      public String outputFieldName() {
        return outputFieldName;
      }
      
      @Override
      public String toString() {
        return String.format("%s", actionFactory);
      }
    };
  }
}
