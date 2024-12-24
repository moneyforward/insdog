package jp.co.moneyforward.autotest.framework.core;

import com.github.dakusui.osynth.ObjectSynthesizer;
import jp.co.moneyforward.autotest.framework.cli.CliUtils;
import jp.co.moneyforward.autotest.framework.utils.InternalUtils;
import org.junit.platform.commons.support.ModifierSupport;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import static com.github.dakusui.osynth.ObjectSynthesizer.methodCall;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.platform.commons.support.ReflectionSupport.invokeMethod;

public interface ExecutionProfile {
  /// 
  /// An annotation that lets the framework know which factory should be used for creating a profile instance.
  /// 
  @SuppressWarnings("rawtypes")
  @Retention(RUNTIME)
  @Target(TYPE)
  @interface CreateWith {
    Class<? extends ExecutionProfile.Factory> value();
  }
  
  interface Factory<T extends ExecutionProfile> {
    /// 
    /// @param branchName This can be `null`, if the execution is not on a branch, otherwise the git branch name.
    /// @return An execution profile object.
    /// 
    T create(String branchName);
  }
  
  static <T extends ExecutionProfile> T create(Class<T> executionProfileClass) {
    var ret = create(createProfileInstance(executionProfileClass), CliUtils.getProfileOverriders(), executionProfileClass);
    logExecutionProfile(ret);
    return ret;
  }
  
  @SuppressWarnings("unchecked")
  static <T extends ExecutionProfile> T createProfileInstance(Class<T> executionProfileClass) {
    try {
      return (T) executionProfileClass.getAnnotation(CreateWith.class)
                                      .value()
                                      .getConstructor()
                                      .newInstance()
                                      .create(InternalUtils.currentBranchName()
                                                           .orElse(null));
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new AutotestException("Failed to instantiate a class:[" + executionProfileClass.getCanonicalName() + "] for: " + e.getMessage(), e);
    }
  }
  
  static <T extends ExecutionProfile> T create(T base, Map<String, String> profileOverriders, Class<T> executionProfileInterfaceClass) {
    if (profileOverriders == null) {
      return base;
    }
    ObjectSynthesizer objectSynthesizer = new ObjectSynthesizer();
    for (Map.Entry<String, String> overrider : profileOverriders.entrySet())
      objectSynthesizer.handle(methodCall(overrider.getKey()).with((self, args) -> profileOverriders.get(overrider.getKey())));
    
    return objectSynthesizer.addInterface(executionProfileInterfaceClass)
                            .synthesize(base)
                            .castTo(executionProfileInterfaceClass);
  }
  
  private static void logExecutionProfile(ExecutionProfile executionProfile) {
    AutotestRunner.LOGGER.info("Execution Profile");
    AutotestRunner.LOGGER.info("----");
    Arrays.stream(executionProfile.getClass().getMethods())
          .filter(ModifierSupport::isPublic)
          .filter(m -> m.getParameters().length == 0)
          .filter(m -> !ModifierSupport.isStatic(m))
          .filter(m -> Objects.equals(executionProfile.getClass(), m.getDeclaringClass()))
          .sorted(Comparator.comparing(Method::getName))
          .forEach(m -> AutotestRunner.LOGGER.info(String.format("- %-30s -> %-30s", m.getName(), InternalUtils.mask(invokeMethod(m, executionProfile)))));
    AutotestRunner.LOGGER.info("----");
    AutotestRunner.LOGGER.info("");
  }
}
