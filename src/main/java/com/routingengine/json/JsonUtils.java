package com.routingengine.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


public class JsonUtils
{
    private JsonUtils()
    {
        throw new UnsupportedOperationException("do not instantiate!");
    }
    
    public static JsonPrimitive castToJsonPrimitive(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        if (jsonElement.isJsonNull())
            return null;
        
        if (!jsonElement.isJsonPrimitive())
            throw new IllegalArgumentException("invalid");
        
        return jsonElement.getAsJsonPrimitive();
    }
    
    public static JsonObject castToJsonObject(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        if (jsonElement.isJsonNull())
            return null;
        
        if (!jsonElement.isJsonObject())
            throw new IllegalArgumentException("invalid");
        
        return jsonElement.getAsJsonObject();
    }
    
    public static JsonArray castToJsonArray(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        if (jsonElement.isJsonNull())
            return null;
        
        if (!jsonElement.isJsonArray())
            throw new IllegalArgumentException("invalid");
        
        return jsonElement.getAsJsonArray();
    }
    
    public static String castToString(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonPrimitive jsonPrimitive = castToJsonPrimitive(jsonElement);
        
        return castToString(jsonPrimitive);
    }
    
    public static Integer castToInt(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonPrimitive jsonPrimitive = castToJsonPrimitive(jsonElement);
        
        return castToInt(jsonPrimitive);
    }
    
    public static Boolean castToBoolean(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonPrimitive jsonPrimitive = castToJsonPrimitive(jsonElement);
        
        return castToBoolean(jsonPrimitive);
    }
    
    public static List<String> castToStringList(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonArray jsonArray = castToJsonArray(jsonElement);
        
        return castToStringList(jsonArray);
    }
    
    public static List<Integer> castToIntList(JsonElement jsonElement)
    {        
        if (jsonElement == null)
            return null;
        
        JsonArray jsonArray = castToJsonArray(jsonElement);
        
        return castToIntList(jsonArray);
    }
    
    public static List<Boolean> castToBooleanList(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonArray jsonArray = castToJsonArray(jsonElement);
        
        return castToBooleanList(jsonArray);
    }
    
    public static Map<String, String> castToStringMap(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonObject jsonObject = castToJsonObject(jsonElement);
        
        return castToStringMap(jsonObject);
    }
    
    public static Map<String, Integer> castToIntMap(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonObject jsonObject = castToJsonObject(jsonElement);
        
        return castToIntMap(jsonObject);
    }
    
    public static Map<String, Boolean> castToBooleanMap(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return null;
        
        JsonObject jsonObject = castToJsonObject(jsonElement);
        
        return castToBooleanMap(jsonObject);
    }
    
    public static String castToString(JsonPrimitive jsonPrimitive)
    {
        if (jsonPrimitive == null)
            return null;
        
        return jsonPrimitive.getAsString();
    }
    
    public static Integer castToInt(JsonPrimitive jsonPrimitive)
    {
        if (jsonPrimitive == null)
            return null;
        
        String value = castToString(jsonPrimitive);
        
        return Integer.parseInt(value);
    }
    
    public static Boolean castToBoolean(JsonPrimitive jsonPrimitive)
    {
        if (jsonPrimitive == null)
            return null;
        
        String value = castToString(jsonPrimitive);
        
        if (!value.matches("true|false"))
            throw new IllegalArgumentException("must be true or false");
        
        return Boolean.parseBoolean(value);
    }
    
    public static List<String> castToStringList(JsonArray jsonArray)
    {
        if (jsonArray == null)
            return null;
        
        List<String> arrayFromJson = new ArrayList<>();
        
        jsonArray.forEach(jsonElement -> arrayFromJson.add(castToString(jsonElement)));
        
        return arrayFromJson;
    } 
    
    public static List<Integer> castToIntList(JsonArray jsonArray)
    {
        if (jsonArray == null)
            return null;
        
        List<Integer> arrayFromJson = new ArrayList<>();
        
        jsonArray.forEach(jsonElement -> arrayFromJson.add(castToInt(jsonElement)));
        
        return arrayFromJson;
    } 
    
    public static List<Boolean> castToBooleanList(JsonArray jsonArray)
    {
        if (jsonArray == null)
            return null;
        
        List<Boolean> arrayFromJson = new ArrayList<>();
        
        jsonArray.forEach(jsonElement -> arrayFromJson.add(castToBoolean(jsonElement)));
        
        return arrayFromJson;
    } 
    
    public static Map<String, String> castToStringMap(JsonObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        
        Map<String, String> mapFromJson = new HashMap<>();
        
        jsonObject.keySet().forEach(key -> mapFromJson.put(key, getAsString(jsonObject, key)));
        
        return mapFromJson;
    }
    
    public static Map<String, Integer> castToIntMap(JsonObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        
        Map<String, Integer> mapFromJson = new HashMap<>();
        
        jsonObject.keySet().forEach(key -> mapFromJson.put(key, getAsInt(jsonObject, key)));
        
        return mapFromJson;
    }
    
    public static Map<String, Boolean> castToBooleanMap(JsonObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        
        Map<String, Boolean> mapFromJson = new HashMap<>();
        
        jsonObject.keySet().forEach(key -> mapFromJson.put(key, getAsBoolean(jsonObject, key)));
        
        return mapFromJson;
    }
    
    public static JsonPrimitive getAsJsonPrimitive(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToJsonPrimitive(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonPrimitive getAsJsonPrimitive(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToJsonPrimitive(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonPrimitive getAsJsonPrimitive(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsJsonPrimitive(castToJsonObject(jsonElement), property);
    }
    
    public static JsonObject getAsJsonObject(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToJsonObject(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonObject getAsJsonObject(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToJsonObject(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonObject getAsJsonObject(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsJsonObject(castToJsonObject(jsonElement), property);
    }
    
    public static JsonArray getAsJsonArray(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToJsonArray(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonArray getAsJsonArray(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToJsonArray(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static JsonArray getAsJsonArray(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsJsonArray(castToJsonObject(jsonElement), property);
    }
    
    public static String getAsString(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToString(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static String getAsString(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToString(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static String getAsString(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsString(castToJsonObject(jsonElement), property);
    }
    
    public static Integer getAsInt(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToInt(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Integer getAsInt(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToInt(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Integer getAsInt(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsInt(castToJsonObject(jsonElement), property);
    }
    
    public static Boolean getAsBoolean(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToBoolean(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Boolean getAsBoolean(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToBoolean(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Boolean getAsBoolean(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsBoolean(castToJsonObject(jsonElement), property);
    }
    
    public static List<String> getAsStringList(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToStringList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<String> getAsStringList(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToStringList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<String> getAsStringList(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsStringList(castToJsonObject(jsonElement), property);
    }
    
    public static List<Integer> getAsIntList(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToIntList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<Integer> getAsIntList(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToIntList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<Integer> getAsIntList(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsIntList(castToJsonObject(jsonElement), property);
    }
    
    public static List<Boolean> getAsBooleanList(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToBooleanList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<Boolean> getAsBooleanList(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToBooleanList(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static List<Boolean> getAsBooleanList(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsBooleanList(castToJsonObject(jsonElement), property);
    }
    
    public static Map<String, String> getAsStringMap(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToStringMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, String> getAsStringMap(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToStringMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, String> getAsStringMap(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsStringMap(castToJsonObject(jsonElement), property);
    }
    
    public static Map<String, Integer> getAsIntMap(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToIntMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, Integer> getAsIntMap(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToIntMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, Integer> getAsIntMap(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsIntMap(castToJsonObject(jsonElement), property);
    }
    
    public static Map<String, Boolean> getAsBooleanMap(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        try {
            return castToBooleanMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, Boolean> getAsBooleanMap(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        try {
            return castToBooleanMap(jsonElement);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(sanitize(property) + " " + exception.getMessage());
        }
    }
    
    public static Map<String, Boolean> getAsBooleanMap(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getAsBooleanMap(castToJsonObject(jsonElement), property);
    }
    
    public static String getToString(JsonRequest request, String property)
    {
        if (request == null || property == null || !request.hasArgument(property))
            return null;
        
        JsonElement jsonElement = request.getArgument(property);
        
        return toString(jsonElement);
    }
    
    public static String getToString(JsonObject jsonObject, String property)
    {
        if (jsonObject == null || property == null || !jsonObject.has(property))
            return null;
        
        JsonElement jsonElement = jsonObject.get(property);
        
        return toString(jsonElement);
    }
    
    public static String getToString(JsonElement jsonElement, String property)
    {
        if (jsonElement == null || property == null)
            return null;
        
        return getToString(castToJsonObject(jsonElement), property);
    }
    
    public static String toString(JsonElement jsonElement)
    {
        if (jsonElement == null)
            return "null";
        
        if (jsonElement.isJsonObject())
            return toString(jsonElement.getAsJsonObject());
        
        if (jsonElement.isJsonArray())
            return toString(jsonElement.getAsJsonArray());
        
        if (jsonElement.isJsonPrimitive())
            return toString(jsonElement.getAsJsonPrimitive());
        
        return jsonElement.toString();
    }
    
    public static String toString(JsonPrimitive jsonPrimitive)
    {
        if (jsonPrimitive == null)
            return "null";
        
        return jsonPrimitive.getAsString();
    }
    
    public static String toString(JsonObject jsonObject)
    {
        if (jsonObject == null)
            return "null";
        
        return "{" + String.join(", ", jsonObject.entrySet().stream()
                .map(entry -> (entry.getKey().toString() + ":" + toString(entry.getValue())))
                .toArray(String[]::new)) + "}";
    }
    
    public static String toString(JsonArray jsonArray)
    {
        if (jsonArray == null)
            return "null";
        
        return "[" + String.join(", ", StreamSupport.stream(jsonArray.spliterator(), false)
                .map(jsonElement -> toString(jsonElement))
                .toArray(String[]::new)) + "]";
    }
    
    public static boolean equals(JsonElement jsonElement1, JsonElement jsonElement2)
    {
        if (jsonElement1 == jsonElement2)
            return true;
        
        if (jsonElement1 == null || jsonElement2 == null)
            return false;
        
        if (jsonElement1.isJsonNull() && jsonElement2.isJsonNull())
            return true;
        
        if (jsonElement1.isJsonPrimitive() && jsonElement2.isJsonPrimitive())
            return equals(jsonElement1.getAsJsonPrimitive(), jsonElement2.getAsJsonPrimitive());
        
        if (jsonElement1.isJsonArray() && jsonElement2.isJsonArray())
            return equals(jsonElement1.getAsJsonArray(), jsonElement2.getAsJsonArray());
        
        if (jsonElement1.isJsonObject() && jsonElement2.isJsonObject())
            return equals(jsonElement1.getAsJsonObject(), jsonElement2.getAsJsonObject());
        
        return false;
    }
    
    public static boolean equals(JsonPrimitive jsonPrimitive1, JsonPrimitive jsonPrimitive2)
    {
        if (jsonPrimitive1 == jsonPrimitive2)
            return true;
        
        if (jsonPrimitive1 == null || jsonPrimitive2 == null)
            return false;
        
        if (jsonPrimitive1.isBoolean() && jsonPrimitive2.isBoolean())
            return jsonPrimitive1.getAsBoolean() == jsonPrimitive2.getAsBoolean();
        
        if (jsonPrimitive1.isNumber() && jsonPrimitive2.isNumber())
            return jsonPrimitive1.getAsBigDecimal().equals(jsonPrimitive2.getAsBigDecimal());
        
        if (jsonPrimitive1.isString() && jsonPrimitive2.isString())
            return jsonPrimitive1.getAsString().equals(jsonPrimitive2.getAsString());
        
        return false;
    }
    
    public static boolean equals(JsonArray jsonArray1, JsonArray jsonArray2)
    {
        if (jsonArray1 == jsonArray2)
            return true;
        
        if (jsonArray1 == null || jsonArray2 == null)
            return false;
        
        if (jsonArray1.size() != jsonArray2.size())
            return false;
        
        for (int i = 0; i < jsonArray1.size(); i++) {
            if (!equals(jsonArray1.get(i), jsonArray2.get(i)))
                return false;
        }
        
        return true;
    }
    
    public static boolean equals(JsonObject jsonObject1, JsonObject jsonObject2)
    {
        if (jsonObject1 == jsonObject2)
            return true;
        
        if (jsonObject1 == null || jsonObject2 == null)
            return false;
        
        if (jsonObject1.size() != jsonObject2.size())
            return false;
        
        for (String property : jsonObject1.keySet()) {
            if (!jsonObject2.has(property))
                return false;
            
            if (!equals(jsonObject1.get(property), jsonObject2.get(property)))
                return false;                    
        }
        
        return true;
    }
    
    public static JsonPrimitive toJsonPrimitive(String string)
    {
        if (string == null)
            return null;
        
        return new JsonPrimitive(string);
    }
    
    public static JsonPrimitive toJsonPrimitive(Boolean bool)
    {
        if (bool == null)
            return null;
        
        return new JsonPrimitive(bool);
    }
    
    public static JsonPrimitive toJsonPrimitive(Number number)
    {
        if (number == null)
            return null;
        
        return new JsonPrimitive(number);
    }
    
    public static JsonArray toJsonArray(@SuppressWarnings("rawtypes") List list)
    {
        return toJsonElement(list).getAsJsonArray();
    }
    
    public static JsonObject toJsonObject(@SuppressWarnings("rawtypes") Map mapping)
    {
        return toJsonElement(mapping).getAsJsonObject();
    }
    
    public static JsonElement toJsonElement(String string)
    {
        return toJsonPrimitive(string);
    }
    
    public static JsonElement toJsonElement(Boolean bool)
    {
        return toJsonPrimitive(bool);
    }
    
    public static JsonElement toJsonElement(Number number)
    {
        return toJsonPrimitive(number);
    }
    
    public static JsonElement toJsonElement(@SuppressWarnings("rawtypes") List list)
    {
        if (list == null)
            return null;
        
        String jsonString = new Gson().toJson(list);
        
        return JsonParser.parseString(jsonString);
    }
    
    public static JsonElement toJsonElement(@SuppressWarnings("rawtypes") Map mapping)
    {
        if (mapping == null)
            return null;
        
        String jsonString = new Gson().toJson(mapping);
        
        return JsonParser.parseString(jsonString);
    }
    
    private static String sanitize(String argument)
    {
        return argument.replaceAll("_", " ");
    }
}
