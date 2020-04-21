package com.routingengine.methods;

import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class RemoveAgentMethod extends AbstractAgentAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        routingEngine.removeAgent(agent.getUUID());
        
        if (agent.isActivated())
            agent.deactivate();
        
        return JsonResponse.success(request, agent.toJson());
    }
}
