package jp.co.moneyforward.autotest.framework.utils;

import com.google.gson.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonUtils {
  public static JsonObject object(Entry... entries) {
    JsonObject ret = new JsonObject();
    for (Entry each : entries)
      ret.add(each.key(), each.value());
    return ret;
  }
  
  public static JsonArray array(Object... objects) {
    return array(Arrays.stream(objects)
        .map(JsonUtils::json)
        .toArray(JsonElement[]::new));
  }
  
  public static JsonArray array(JsonElement... elements) {
    JsonArray ret = new JsonArray(elements.length);
    for (JsonElement each : elements)
      ret.add(each);
    return ret;
  }
  
  public static Entry $(String key, JsonElement value) {
    return entry(key, value);
  }
  
  public static Entry entry(String key, JsonElement value) {
    return new Entry(key, value);
  }
  
  public static JsonElement json(Object object) {
    if (object instanceof JsonElement)
      return (JsonElement) object;
    if (object instanceof Number)
      return new JsonPrimitive((Number) object);
    if (object instanceof String)
      return new JsonPrimitive((String) object);
    if (object instanceof Boolean)
      return new JsonPrimitive((Boolean) object);
    if (object == null)
      return JsonNull.INSTANCE;
    throw new RuntimeException();
  }
  
  public static JsonPath jsonpath(Object... pathComponents) {
     return new JsonPath(pathComponents);

   }
  
  public static String prettyPrintJsonElement(JsonObject summarizedValue) {
      return new GsonBuilder().setPrettyPrinting().create().toJson(summarizedValue);
    }
  
  public static JsonObject merge(JsonObject... objs) {
    JsonObject ret = new JsonObject();
    ExtendJsonObject.extendJsonObject(ret, ConflictStrategy.PREFER_SECOND_OBJ, objs);
    return ret;
  }
  
  public enum ConflictStrategy {
    
    THROW_EXCEPTION, PREFER_FIRST_OBJ, PREFER_SECOND_OBJ, PREFER_NON_NULL;
  }
  
  enum ExtendJsonObject {
    ;
    
    /**
     * Ignores {@code null} in objs will be ignored.
     *
     * @param destinationObject An object to which the merge result is accumulated.
     * @param conflictResolutionStrategy A strategy to resolve a conflict detected during a merge.
     * @param objs {@code JsonObject}s to be merged.
     * @throws JsonObjectExtensionConflictException Failed to resolve a conflict during the execution.
     */
    public static void extendJsonObject(JsonObject destinationObject, ConflictStrategy conflictResolutionStrategy, JsonObject ... objs)
        throws JsonObjectExtensionConflictException {
      for (JsonObject obj : objs) {
        if (obj == null)
          continue;
        extendJsonObject(destinationObject, obj, conflictResolutionStrategy);
      }
    }
    
    private static void extendJsonObject(JsonObject leftObj, JsonObject rightObj, ConflictStrategy conflictStrategy)
        throws JsonObjectExtensionConflictException {
      for (Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet()) {
        String rightKey = rightEntry.getKey();
        JsonElement rightVal = rightEntry.getValue();
        if (leftObj.has(rightKey)) {
          //conflict
          JsonElement leftVal = leftObj.get(rightKey);
          if (leftVal.isJsonArray() && rightVal.isJsonArray()) {
            JsonArray leftArr = leftVal.getAsJsonArray();
            JsonArray rightArr = rightVal.getAsJsonArray();
            //concat the arrays -- there cannot be a conflict in an array, it's just a collection of stuff
            for (int i = 0; i < rightArr.size(); i++) {
              leftArr.add(rightArr.get(i));
            }
          } else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
            //recursive merging
            extendJsonObject(leftVal.getAsJsonObject(), rightVal.getAsJsonObject(), conflictStrategy);
          } else {//not both arrays or objects, normal merge with conflict resolution
            handleMergeConflict(rightKey, leftObj, leftVal, rightVal, conflictStrategy);
          }
        } else {//no conflict, add to the object
          leftObj.add(rightKey, rightVal);
        }
      }
    }
    
    private static void handleMergeConflict(String key, JsonObject leftObj, JsonElement leftVal, JsonElement rightVal, ConflictStrategy conflictStrategy)
        throws JsonObjectExtensionConflictException {
      {
        switch (conflictStrategy) {
          case PREFER_FIRST_OBJ:
            break;//do nothing, the right val gets thrown out
          case PREFER_SECOND_OBJ:
            leftObj.add(key, rightVal);//right side auto-wins, replace left val with its val
            break;
          case PREFER_NON_NULL:
            //check if right side is not null, and left side is null, in which case we use the right val
            if (leftVal.isJsonNull() && !rightVal.isJsonNull()) {
              leftObj.add(key, rightVal);
            }//else do nothing since either the left value is non-null or the right value is null
            break;
          case THROW_EXCEPTION:
            throw new JsonObjectExtensionConflictException("Key " + key + " exists in both objects and the conflict resolution strategy is " + conflictStrategy);
          default:
            throw new UnsupportedOperationException("The conflict strategy " + conflictStrategy + " is unknown and cannot be processed");
        }
      }
    }
  }
  
  public static class JsonObjectExtensionConflictException extends RuntimeException {
    
    public JsonObjectExtensionConflictException(String message) {
      super(message);
    }
  }
  
  public record Entry(String key, JsonElement value) {
  }
  
  public record JsonPath(Object... pathComponents) {
    List<Object> toList() {
      return Arrays.stream(pathComponents).toList();
    }

    @Override
    public String toString() {
      return Arrays.stream(this.pathComponents()).map(Objects::toString).collect(Collectors.joining(".", ".", ""));
    }
  }
}
