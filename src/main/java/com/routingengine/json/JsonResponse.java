package com.routingengine.json;

import static com.routingengine.MethodManager.supports;
import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonUtils.getAsInt;
import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonProtocol.readJsonResponse;
import static com.routingengine.json.JsonProtocol.writeJsonResponse;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;


public class JsonResponse
{
    public static final String NULL_METHOD = "-";
    public static final Integer NULL_TICKET_NUMBER = -1;
    public static final JsonElement NULL_PAYLOAD = JsonNull.INSTANCE;
    private Integer ticketNumber = null;
    private String method = null;
    private Result result = null;
    private JsonElement payload = null;
    
    public Integer getTicketNumber()
    {
        return ticketNumber;
    }
    
    public JsonResponse setTicketNumber(Integer ticketNumber)
    {
        this.ticketNumber = ticketNumber;
        
        return this;
    }
    
    public JsonResponse setNullTicketNumber()
    {
        return setTicketNumber(NULL_TICKET_NUMBER);
    }
    
    public boolean hasTicketNumber()
    {
        return ticketNumber != null;
    }
    
    public boolean hasValidTicketNumber()
    {
        return hasTicketNumber() && (ticketNumber > 0 || ticketNumber == NULL_TICKET_NUMBER);
    }
    
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
        return supports(method) || NULL_METHOD.equals(method);
    }
    
    public JsonResponse setMethod(String method)
    {
        this.method = method;
        
        return this;
    }
    
    public JsonResponse setNullMethod()
    {
        return setMethod(NULL_METHOD);
    }
    
    public Result getResult()
    {
        return result;
    }
    
    public boolean hasResult()
    {
        return result != null;
    }
    
    public boolean hasValidResult()
    {
        return hasResult();
    }
    
    public JsonResponse setSucceed()
    {
        return setResult(Result.SUCCESS);
    }
    
    public JsonResponse setPending()
    {
        return setResult(Result.PENDING);
    }
    
    public JsonResponse setFail()
    {
        return setResult(Result.FAILURE);
    }
    
    public JsonResponse setResult(String result)
    {
        return setResult(Result.fromName(result));
    }
    
    public JsonResponse setResult(Result result)
    {
        this.result = result;
        
        return this;
    }
    
    public boolean didSucceed()
    {
        return result == Result.SUCCESS;
    }
    
    public boolean isPending()
    {
        return result == Result.PENDING;
    }
    
    public boolean didFail()
    {
        return result == Result.FAILURE;
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
    
    public JsonResponse setNullPayload()
    {
        return setPayload(NULL_PAYLOAD);
    }
    
    public boolean equals(JsonResponse other)
    {
        if (!ticketNumber.equals(other.ticketNumber))
            return false;
        
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
        Integer ticketNumber = getAsInt(jsonObject, "ticket_number");
        
        String method = getAsString(jsonObject, "method");
        
        String result = getAsString(jsonObject, "result");
        
        JsonElement payload = jsonObject.get("payload");
        
        JsonResponse response = new JsonResponse()
            .setTicketNumber(ticketNumber)
            .setMethod(method)
            .setResult(result)
            .setPayload(payload);
        
        return response;
    }
    
    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.addProperty("ticket_number", ticketNumber);
        jsonObject.addProperty("method", method);
        jsonObject.addProperty("result", result.toString());
        jsonObject.add("payload", payload);
        
        return jsonObject;
    }
    
    public void ensureWellFormed()
        throws JsonProtocolException
    {
        if (!hasTicketNumber())
            throw new JsonProtocolException("missing ticket number");
        
        if (!hasValidTicketNumber())
            throw new JsonProtocolException("invalid ticket number");
        
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
    
    public void readFrom(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        readJsonResponse(jsonReader, this);
    }
    
    public static JsonResponse fromReader(JsonReader jsonReader)
        throws IOException, JsonProtocolException
    {
        JsonResponse jsonResponse = new JsonResponse();
        
        jsonResponse.readFrom(jsonReader);
        
        return jsonResponse;
    }
    
    public boolean writeTo(JsonWriter jsonWriter)
        throws IOException
    {
        try {
            writeJsonResponse(this, jsonWriter);
            
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
            .setSucceed()
            .setPayload(payload);
    }
    
    public static JsonResponse pending(JsonRequest jsonRequest)
    {
        return new JsonResponse()
            .setMethod(jsonRequest.getMethod())
            .setPending()
            .setNullPayload();
    }
    
    public static JsonResponse failure(JsonRequest jsonRequest, Exception exception)
    {
        return failure(jsonRequest, exception.getMessage());
    }
    
    public static JsonResponse failure(JsonRequest jsonRequest, String errorMessage)
    {
        return new JsonResponse()
            .setMethod(jsonRequest.getMethod())
            .setFail()
            .setPayload(errorMessage);
    }
    
    public static JsonResponse failure(String errorMessage)
    {
        return new JsonResponse()
            .setNullTicketNumber()
            .setNullMethod()
            .setFail()
            .setPayload(errorMessage);
    }
    
    public static enum Result {
        SUCCESS("success"),
        FAILURE("failure"),
        PENDING("pending");
        
        private final String name;
        
        Result(String name)
        {
            this.name = name;
        }
        
        public static Result fromName(String resultName)
        {
            for (Result result : Result.values()) {
                if (result.name.equals(resultName))
                    return result;
            }
            
            throw new IllegalArgumentException("result name invalid");
        }
        
        @Override
        public String toString()
        {
            return name;
        }
    }
}
