package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.RoutingEngine;


public interface Method
{
    public void setRoutingEngine(RoutingEngine routingEngine);
    
    public abstract JsonElement handleArguments(JsonObject arguments);
}
