package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public abstract class AbstractAgentMethod extends AbstractAdminMethod
{
    protected final Agent getAgent(JsonObject arguments)
    {
        Agent agent;
        
        try {
            agent = getAgentWithUUID(arguments);
        }
        
        catch (IllegalArgumentException exception) {
            ensureMissingException(exception);
            
            try {
                agent = getAgentWithRainbowId(arguments);
            }
            
            catch (IllegalArgumentException exception2) {
                ensureMissingException(exception2);
                
                throw new IllegalArgumentException("uuid or rainbow id missing");
            }
        }
        
        updateAddress(agent, arguments);
        
        return agent;
    }
    
    private final Agent getAgentWithUUID(JsonObject arguments)
    {
        String agentUUIDString = getAsString(arguments, "uuid");
        
        return routingEngine.getAgent(agentUUIDString);
    }
    
    private final Agent getAgentWithRainbowId(JsonObject arguments)
    {
        String rainbowIdString = getAsString(arguments, "rainbow_id");
        
        return routingEngine.getAgentFromRainbowId(rainbowIdString);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
