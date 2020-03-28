package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class RemoveAgentMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = removeAgent(arguments);
        
        return agent.toJson();
    }
    
    public Agent removeAgent(JsonObject arguments)
    {
        String agentUUIDString = getAsString(arguments, "uuid");
        
        Agent agent = routingEngine.removeAgent(agentUUIDString);
        
        if (agent.isActivated())
            agent.deactivate();
        
        return agent;
    }
}
