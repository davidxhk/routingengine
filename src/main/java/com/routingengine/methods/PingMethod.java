package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class PingMethod extends AbstractAdminMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        return new JsonPrimitive("pong");
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
