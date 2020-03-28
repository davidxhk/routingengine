package com.routingengine.json;

import static com.routingengine.MethodManager.supports;
import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonProtocol.readJsonResponse;
import static com.routingengine.json.JsonProtocol.writeJsonResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class JsonResponse
{
    private String method = null;
    private String result = null;
    private JsonElement payload = null;
    
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
        return supports(method) || "null".equals(method);
    }
    
    public JsonResponse setMethod(String method)
    {
        this.method = method;
        
        return this;
    }
    
    public String getResult()
    {
        return result;
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    public boolean hasValidResult()
    {
        return result.matches("success|failure|unknown");
    }
    
    public JsonResponse setResult(String result)
    {
        this.result = result;
        
        return this;
    }
    
    public boolean didSucceed()
    {
        return "success".equals(result);
    }
    
    public JsonElement getPayload()
    {
        return payload;
    }
    
    public boolean hasPayload()
    {
        return payload != null;
    }
    
    public JsonResponse setPayload(String payload)
    {
        setPayload(toJsonElement(payload));
        
        return this;
    }
    
    public JsonResponse setPayload(Boolean payload)
    {
        setPayload(toJsonElement(payload));
        
        return this;
    }
    
    public JsonResponse setPayload(Number payload)
    {
        setPayload(toJsonElement(payload));
        
        return this;
    }
    
    public JsonResponse setPayload(@SuppressWarnings("rawtypes") List payload)
    {
        setPayload(toJsonElement(payload));
        
        return this;
    }
    
    public JsonResponse setPayload(@SuppressWarnings("rawtypes") Map payload)
    {
        setPayload(toJsonElement(payload));
        
        return this;
    }
    
    public JsonResponse setPayload(JsonElement payload)
    {
        this.payload = payload;
        
        return this;
    }
    
    public boolean equals(JsonResponse other)
    {
        if (!method.equals(other.method))
            return false;
        
        if (!result.equals(other.result))
            return false;
        
        if (!JsonUtils.equals(payload, other.payload))
            return false;
        
        return true;
    }
    
    public String toString()
    {
        return JsonUtils.toString(toJson());
    }
    
    public static JsonResponse fromJson(JsonObject jsonObject)
    {
        String method = getAsString(jsonObject, "method");
        
        String result = getAsString(jsonObject, "result");
        
        JsonElement payload = jsonObject.get("payload");
        
        JsonResponse response = new JsonResponse()
                .setMethod(method)
                .setResult(result)
                .setPayload(payload);
        
        return response;
    }
    
    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("method", method);
        jsonObject.addProperty("result", result);
        jsonObject.add("payload", payload);
        
        return jsonObject;
    }
    
    public void ensureWellFormed()
        throws JsonProtocolException
    {
        if (!hasMethod())
            throw new JsonProtocolException("missing method");
        
        if (!hasValidMethod())
            throw new JsonProtocolException("invalid method");
        
        if (!hasResult())
            throw new JsonProtocolException("missing result");
        
        if (!hasValidResult())
            throw new JsonProtocolException("invalid result");
        
        if (!hasPayload())
            throw new JsonProtocolException("missing payload");
    }
    
    public void read(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        readJsonResponse(jsonReader, this);
    }
    
    public static JsonResponse fromReader(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        JsonResponse jsonResponse = new JsonResponse();
        
        jsonResponse.read(jsonReader);
        
        return jsonResponse;
    }
    
    public void write(JsonWriter jsonWriter)
        throws IOException, JsonProtocolException
    {
        writeJsonResponse(this, jsonWriter);
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
    
    public static JsonResponse success(JsonRequest jsonRequest, JsonElement payload)
    {
        return new JsonResponse()
            .setMethod(jsonRequest.getMethod())
            .setResult("success")
            .setPayload(payload);
    }
    
    public static JsonResponse failure(JsonRequest jsonRequest, Exception exception)
    {
        return failure(jsonRequest, exception.getMessage());
    }
    
    public static JsonResponse failure(JsonRequest jsonRequest, String errorMessage)
    {
        return new JsonResponse()
            .setMethod(jsonRequest.getMethod())
            .setResult("failure")
            .setPayload(errorMessage);
    }
    
    public static JsonResponse failure(String errorMessage)
    {
        return new JsonResponse()
            .setMethod("null")
            .setResult("failure")
            .setPayload(errorMessage);
    }
}
