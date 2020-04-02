package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBoolean;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class UpdateAgentAvailabilityMethod extends CheckAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);

        Boolean isAvailable = getAsBoolean(arguments, "available");

        agent.setAvailability(isAvailable);

        routingEngine.updateAvailableAgents(agent);

        return agent.toJson();
    }
}
