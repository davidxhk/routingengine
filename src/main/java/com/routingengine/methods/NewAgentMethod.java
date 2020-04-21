package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBooleanMap;
import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewAgentMethod extends AbstractAgentAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = newAgent(request);
        
        routingEngine.addAgent(agent);
        
        return JsonResponse.success(request, agent.toJson());
    }
    
    public Agent newAgent(JsonRequest request)
    {
        return Agent.builder()
            .setRainbowId(getAsString(request, "rainbow_id"))
            .setAddress(getAsString(request, "address"))
            .setSkills(getAsBooleanMap(request, "skills"))
            .build();
    }
}
