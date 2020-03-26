package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class DropSupportRequestMethod extends CheckAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        dropSupportRequest(agent);
        
        return agent.toJson();
    }
    
    public static void dropSupportRequest(Agent agent)
    {
        if (!agent.hasAssignedSupportRequest())
            throw new IllegalStateException("agent has no assigned request");
        
        agent.getAssignedSupportRequest().doublePriority();
        agent.getAssignedSupportRequest().setAssignedAgent(null);
        agent.setAssignedSupportRequest(null);
    }
}
