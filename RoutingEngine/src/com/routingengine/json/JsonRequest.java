package com.routingengine.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.MethodManager;


public class JsonRequest
{
  private String method = null;
  private JsonObject arguments = new JsonObject();
  
  public String getMethod()
  {
    return method;
  }
  
  public boolean hasMethod()
  {
    return method != null;
  }
  
  public boolean hasValidMethod()
  {
    return MethodManager.supports(method);
  }
  
  public JsonRequest setMethod(String method)
  {
    this.method = method;
    
    return this;
  }
  
  public JsonObject getArguments()
  {
    return arguments;
  }
  
  public boolean hasArguments()
  {
    return arguments != null;
  }
  
  public JsonRequest setArguments(JsonObject arguments)
  {
    this.arguments = arguments;
    
    return this;
  }
  
  public JsonElement getArgument(String property)
  {
    return arguments.get(property);
  }
  
  public boolean hasArgument(String property)
  {
    return arguments.has(property);
  }
  
  public JsonRequest setArgument(String property, String value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, jsonElement);
  }
  
  public JsonRequest setArgument(String property, Boolean value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, jsonElement);
  }
  
  public JsonRequest setArgument(String property, Number value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, jsonElement);
  }
  
  public JsonRequest setArgument(String property, @SuppressWarnings("rawtypes") List value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, jsonElement);
  }
  
  public JsonRequest setArgument(String property, @SuppressWarnings("rawtypes") Map value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, jsonElement);
  }
  
  public JsonRequest setArgument(String property, JsonElement value)
  {
    arguments.add(property, value);
    
    return this;
  }
  
  public JsonRequest setArgument(String property, String key, String value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, key, jsonElement);
  }
  
  public JsonRequest setArgument(String property, String key, Boolean value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, key, jsonElement);
  }
  
  public JsonRequest setArgument(String property, String key, Number value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, key, jsonElement);
  }
  
  public JsonRequest setArgument(String property, String key, @SuppressWarnings("rawtypes") List value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, key, jsonElement);
  }
  
  public JsonRequest setArgument(String property, String key, @SuppressWarnings("rawtypes") Map value)
  {
    JsonElement jsonElement = JsonUtils.toJsonElement(value);
    
    return setArgument(property, key, jsonElement);
  }
  
  public JsonRequest setArgument(String property, String key, JsonElement value)
  {
    if (!arguments.has(property))
      arguments.add(property, new JsonObject());
    
    if (!arguments.get(property).isJsonObject())
      throw new IllegalArgumentException("property " + property + " is not json object!");
    
    JsonObject argument = JsonUtils.getAsJsonObject(arguments, property);
    
    argument.add(key, value);
    
    return this;
  }
  
  public boolean equals(JsonRequest other)
  {
    if (!method.equals(other.method))
      return false;
    
    if (!JsonUtils.equals(arguments, other.arguments))
      return false;
    
    return true;
  }
  
  public String toString()
  {
    return method + " " + JsonUtils.toString(arguments);
  }
  
  public void ensureWellFormed()
    throws JsonProtocolException
  {
    if (!hasMethod())
      throw new JsonProtocolException("missing method");
    
    if (!hasValidMethod())
      throw new JsonProtocolException("invalid method");
    
    if (!hasArguments())
      throw new JsonProtocolException("missing arguments");
  }
  
  public void read(JsonReader jsonReader)
    throws IOException, JsonProtocolException
  {
    JsonProtocol.readJsonRequest(jsonReader, this);
  }
  
  public static JsonRequest fromReader(JsonReader jsonReader)
    throws IOException, JsonProtocolException
  {
    JsonRequest jsonRequest = new JsonRequest();
    
    jsonRequest.read(jsonReader);
    
    return jsonRequest;
  }
  
  public boolean publish(JsonWriter jsonWriter)
    throws IOException
  {
    try {
      JsonProtocol.writeJsonRequest(this, jsonWriter);
      
      return true;
    }
    
    catch (JsonProtocolException exception) {
      return false;
    }
  }
  
  public boolean service(MethodManager manager, JsonResponse jsonResponse)
  {
    jsonResponse.setMethod(method);
    
    try {
      JsonElement payload = manager.handle(method, arguments);
      
      jsonResponse
        .setResult("success")
        .setPayload(payload);
      
      return true;
    }
    
    catch (IllegalArgumentException | IllegalStateException exception) {
      jsonResponse
        .setResult("failure")
        .setPayload(exception.getMessage());
      
      return false;
    }
  }
}
