package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;


public class CloseSupportRequestMethod extends CheckSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);

        Agent agent = supportRequest.getAssignedAgent();

        supportRequest.close();

        routingEngine.updateAvailableAgents(agent);

        return supportRequest.toJson();
    }
}
