package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBoolean;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentAvailabilityMethod extends AbstractAgentMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        Boolean isAvailable = getAsBoolean(request, "available");
        
        agent.setAvailability(isAvailable);
        
        return JsonResponse.success(request, agent.toJson());
    }
}
