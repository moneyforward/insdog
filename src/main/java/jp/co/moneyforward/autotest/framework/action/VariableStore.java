package jp.co.moneyforward.autotest.framework.action;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.dakusui.valid8j.Requires.requireNonNull;

public interface VariableStore {
  <V> Optional<V> lookUp(String variableName);
  
  void put(String variableName, Object value);
  
  class Impl implements VariableStore {
    final Map<String, Object> map;
    
    public Impl(Map<String, Object> map) {
      this.map = new HashMap<>(map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> lookUp(String variableName) {
      return this.map.containsKey(requireNonNull(variableName)) ? Optional.of((V) map.get(variableName))
                                                                : Optional.empty();
    }
    
    @Override
    public void put(String variableName, Object value) {
      this.map.put(requireNonNull(variableName), requireNonNull(value));
    }
    
    public void remove(String variableName) {
      this.map.remove(requireNonNull(variableName));
    }
  }
}
