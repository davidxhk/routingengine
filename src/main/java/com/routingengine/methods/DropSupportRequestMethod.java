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

        agent.dropAssignedSupportRequest();

        routingEngine.updateAvailableAgents(agent);

        return agent.toJson();
    }
}
