package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBoolean;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ActivateAgentMethod extends AbstractAgentAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        Boolean doActivate = getAsBoolean(request, "activate");
        
        if (doActivate == null)
            throw new IllegalArgumentException("activate missing");
        
        if (doActivate)
            agent.activate();
        
        else
            agent.deactivate();
        
        return JsonResponse.success(request, agent.toJson());
    }
}