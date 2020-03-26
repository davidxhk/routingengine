package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.routingengine.MethodManager;


public class PingMethod extends MethodManager.Method
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
