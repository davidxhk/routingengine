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
        
        updateAgentAvailability(agent, arguments);
        
        return agent.toJson();
    }
    
    public static void updateAgentAvailability(Agent agent, JsonObject arguments)
    {
        Boolean isAvailable = getAsBoolean(arguments, "available");
        
        agent.setAvailability(isAvailable);
    }
}
