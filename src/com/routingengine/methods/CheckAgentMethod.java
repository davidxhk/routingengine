package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class CheckAgentMethod extends Method
{    
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        return agent.toJson();
    }
    
    public Agent getAgent(JsonObject arguments)
    {
        String agentUUIDString = getAsString(arguments, "uuid");
        
        return routingEngine.getAgent(agentUUIDString);
    }
}
