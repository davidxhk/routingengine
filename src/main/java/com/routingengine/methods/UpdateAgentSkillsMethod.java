package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBooleanMap;
import java.util.Map;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentSkillsMethod extends AbstractAgentAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
        Map<String, Boolean> skills = getAsBooleanMap(request, "skills");
        
        agent.setSkills(skills);
        
        return JsonResponse.success(request, agent.toJson());
    }
}
