package com.routingengine.methods;

import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class PingMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonElement payload = ping();
        
        return payload;
    }
    
    public JsonElement ping()
    {
        return new JsonPrimitive("pong");
    }
}
