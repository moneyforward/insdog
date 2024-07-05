package jp.co.moneyforward.autotest.framework.action;

import com.github.dakusui.actionunit.core.Action;
import com.github.dakusui.actionunit.core.Context;

import static com.github.valid8j.classic.Requires.requireNonNull;

/**
 * An interface that models an occurrence of an action in a test scenario.
 */
public interface Call {
  /**
   * A method to return output field name.
   *
   * @return An output field name of this object.
   */
  String outputFieldName();
  
  /**
   * Converts this call to action to an action object.
   *
   * This is an `Node#accept` method in the **Visitor** pattern.
   * Usually implementations of this method should call back by `actionComposer#create(this)` to make a double dispatch happen.
   *
   * @param actionComposer A visitor, which creates an action from this object.
   * @return An action created by `actionComposer`.
   */
  Action toAction(ActionComposer actionComposer);
  
  abstract class ActCall<T, R> implements Call {
    private final String inputFieldName;
    private final String outputFieldName;
    
    protected ActCall(String inputFieldName, String outputFieldName) {
      this.inputFieldName = requireNonNull(inputFieldName);
      this.outputFieldName = requireNonNull(outputFieldName);
    }
    
    String inputFieldName() {
      return this.inputFieldName;
    }
    
    /**
     * Returns an input field's value of the `sceneCall`.
     *
     * @param sceneCall A `sceneCall` whose input field value is returned.
     * @param context   A context, in which the `sceneCall` 's input field value is resolved.
     * @return A value of an input field name of a `sceneCall`.
     */
    @SuppressWarnings("unchecked")
    T inputFieldValue(SceneCall sceneCall, Context context) {
      return (T) sceneCall.workArea(context).get(inputFieldName());
    }
    
    @Override
    public String outputFieldName() {
      return this.outputFieldName;
    }
  }
  
  class PipelinedActCall<T, R> extends ActCall<T, R> {
    
    private final PipelinedAct<T, ?, R> act;
    
    public PipelinedActCall(String inputFieldName, PipelinedAct<T, ?, R> act, String outputFieldName) {
      super(inputFieldName, outputFieldName);
      this.act = act;
    }
    
    public PipelinedAct<T, ?, R> act() {
      return this.act;
    }
    
    @Override
    public Action toAction(ActionComposer actionComposer) {
      return actionComposer.create(this);
    }
  }
}
