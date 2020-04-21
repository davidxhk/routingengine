package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;


public abstract class AbstractAgentMethod extends AbstractAdminMethod
{
    protected final Agent getAgent(JsonRequest request)
    {
        Agent agent;
        
        try {
            agent = getAgentWithUUID(request);
        }
        
        catch (IllegalArgumentException exception) {
            ensureMissingException(exception);
            
            try {
                agent = getAgentWithRainbowId(request);
            }
            
            catch (IllegalArgumentException exception2) {
                ensureMissingException(exception2);
                
                throw new IllegalArgumentException("uuid or rainbow id missing");
            }
        }
        
        updateAddress(agent, request);
        
        return agent;
    }
    
    private final Agent getAgentWithUUID(JsonRequest request)
    {
        String agentUUIDString = getAsString(request, "uuid");
        
        return routingEngine.getAgent(agentUUIDString);
    }
    
    private final Agent getAgentWithRainbowId(JsonRequest request)
    {
        String rainbowIdString = getAsString(request, "rainbow_id");
        
        return routingEngine.getAgentFromRainbowId(rainbowIdString);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
