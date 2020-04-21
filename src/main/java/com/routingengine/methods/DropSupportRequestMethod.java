package com.routingengine.methods;

import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class DropSupportRequestMethod extends AbstractAgentMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        agent.dropAssignedSupportRequest();
        
        return JsonResponse.success(request, agent.toJson());
    }
}
