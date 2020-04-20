package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class CheckAgentMethod extends AbstractAgentMethod
{    
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        return agent.toJson();
    }
}
