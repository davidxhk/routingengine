package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBoolean;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class ActivateAgentMethod extends CheckAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        Boolean doActivate = getAsBoolean(arguments, "activate");
        
        if (doActivate)
            agent.activate();
        
        else
            agent.deactivate();
        
        return agent.toJson();
    }
}