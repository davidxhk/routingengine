package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class RemoveAgentMethod extends AbstractAgentAdminMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        routingEngine.removeAgent(agent.getUUID());
        
        if (agent.isActivated())
            agent.deactivate();
        
        return agent.toJson();
    }
}
