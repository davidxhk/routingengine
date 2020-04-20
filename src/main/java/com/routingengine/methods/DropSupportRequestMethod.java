package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class DropSupportRequestMethod extends AbstractAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        agent.dropAssignedSupportRequest();
        
        return agent.toJson();
    }
}
