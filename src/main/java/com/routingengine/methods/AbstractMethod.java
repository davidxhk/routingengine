package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.InetEntity;
import com.routingengine.RoutingEngine;


public abstract class AbstractMethod
    implements Method
{
    protected RoutingEngine routingEngine;
    
    @Override
    public void setRoutingEngine(RoutingEngine routingEngine)
    {
        this.routingEngine = routingEngine;
    }
    
    @Override
    public final JsonElement handleArguments(JsonObject arguments)
    {
        arguments = beforeHandle(arguments);
        
        JsonElement payload = handle(arguments);
        
        return afterHandle(arguments, payload);
    }
    
    protected JsonObject beforeHandle(JsonObject arguments)
    {
        return arguments;
    }
    
    protected abstract JsonElement handle(JsonObject arguments);
    
    protected JsonElement afterHandle(JsonObject arguments, JsonElement payload)
    {
        return payload;
    }
    
    protected static final void ensureMissingException(IllegalArgumentException exception)
    {
        String message = exception.getMessage();
        
        if (!message.contains("missing"))
            throw exception;
    }
    
    protected static final void updateAddress(InetEntity entity, JsonObject arguments)
    {
        String address = getAsString(arguments, "address");
        
        entity.setAddress(address);
    }
}
