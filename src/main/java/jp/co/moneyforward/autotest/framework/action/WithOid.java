package jp.co.moneyforward.autotest.framework.action;

public interface WithOid {
  /// 
  /// Returns an object identifier of this object.
  /// 
  /// @return An object identifier of this object.
  /// 
  String oid();
  
  /// 
  /// Returns a working variable store name for the object ID of this instance.
  /// 
  /// @return A working variable store name.
  /// 
  default String workingVariableStoreName() {
    String objectId = oid();
    return "work-" + objectId;
  }
}
