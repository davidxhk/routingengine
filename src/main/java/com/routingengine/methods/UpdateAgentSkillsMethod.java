package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsBooleanMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class UpdateAgentSkillsMethod extends CheckAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        Map<String, Boolean> skills = getAsBooleanMap(arguments, "skills");
        
        agent.setSkills(skills);
        
        return agent.toJson();
    }
}
