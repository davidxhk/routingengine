package com.routingengine.methods;

import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class CheckAgentMethod extends AbstractAgentMethod
{    
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        return JsonResponse.success(request, agent.toJson());
    }
}
