package com.routingengine.json;

import static com.routingengine.MethodManager.supports;
import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonUtils.getAsJsonObject;
import static com.routingengine.json.JsonProtocol.readJsonRequest;
import static com.routingengine.json.JsonProtocol.writeJsonRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


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
        return method == "null" || supports(method);
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
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, jsonElement);
    }
    
    public JsonRequest setArgument(String property, Boolean value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, jsonElement);
    }
    
    public JsonRequest setArgument(String property, Number value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, jsonElement);
    }
    
    public JsonRequest setArgument(String property, @SuppressWarnings("rawtypes") List value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, jsonElement);
    }
    
    public JsonRequest setArgument(String property, @SuppressWarnings("rawtypes") Map value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, jsonElement);
    }
    
    public JsonRequest setArgument(String property, JsonElement value)
    {
        arguments.add(property, value);
        
        return this;
    }
    
    public JsonRequest setArgument(String property, String key, String value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, key, jsonElement);
    }
    
    public JsonRequest setArgument(String property, String key, Boolean value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, key, jsonElement);
    }
    
    public JsonRequest setArgument(String property, String key, Number value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, key, jsonElement);
    }
    
    public JsonRequest setArgument(String property, String key, @SuppressWarnings("rawtypes") List value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, key, jsonElement);
    }
    
    public JsonRequest setArgument(String property, String key, @SuppressWarnings("rawtypes") Map value)
    {
        JsonElement jsonElement = toJsonElement(value);
        
        return setArgument(property, key, jsonElement);
    }
    
    public JsonRequest setArgument(String property, String key, JsonElement value)
    {
        if (!arguments.has(property))
            arguments.add(property, new JsonObject());
        
        if (!arguments.get(property).isJsonObject())
            throw new IllegalArgumentException("property " + property + " is not json object!");
        
        JsonObject argument = getAsJsonObject(arguments, property);
        
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
        readJsonRequest(jsonReader, this);
    }
    
    public static JsonRequest fromReader(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        JsonRequest jsonRequest = new JsonRequest();
        
        jsonRequest.read(jsonReader);
        
        return jsonRequest;
    }
    
    public void write(JsonWriter jsonWriter)
        throws IOException, JsonProtocolException
    {
        writeJsonRequest(this, jsonWriter);
    }
    
    public boolean writeSafe(JsonWriter jsonWriter)
        throws IOException
    {
        try {
            write(jsonWriter);
            
            return true;
        }
        
        catch (JsonProtocolException exception) {
            return false;
        }
    }
}
