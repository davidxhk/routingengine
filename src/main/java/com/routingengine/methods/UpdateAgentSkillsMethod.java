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
        
        updateAgentSkills(agent, arguments);
        
        return agent.toJson();
    }
    
    public static void updateAgentSkills(Agent agent, JsonObject arguments)
    {
        Map<String, Boolean> skills = getAsBooleanMap(arguments, "skills");
        
        agent.setSkills(skills);
    }
}
