package com.routingengine.examples;

import java.io.IOException;
import java.util.Map;
import com.routingengine.Logger;
import com.routingengine.json.JsonResponse;


public class AgentClientConnectionHandler extends CustomerClientConnectionHandler
{
    public int numberOfSupportRequestsToService;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException
    {
        log("initialized!");
        
        randomSleep();
        
        newAgent("agent_" + id, Map.of(type, true));
        
        JsonResponse response = awaitResponse();
        
        String agentUUIDString = getAgent(response).getUUID().toString();
        
        log("uuid -> " + agentUUIDString);
        
        randomSleep();
        
        updateAgentAvailabilityWithUUID(agentUUIDString, true);
        
        awaitResponse();
        
        randomSleep();
        
        for (int i = 0; i < numberOfSupportRequestsToService; i++) {
            takeSupportRequestWithUUID(agentUUIDString);
            
            response = awaitResponse();
            
            if (!response.didSucceed()) {
                ensureFailedResponseHasErrorPayload(response, "take support request timeout");
                
                continue;
            }
            
            String supportRequestUUIDString = getAssignedSupportRequest(response).getUUID().toString();
            
            randomSleep();
            
            closeSupportRequest(supportRequestUUIDString);
            
            awaitResponse();
        }
        
        exit();
    }
    
    @Override
    protected void log(String message)
    {
        Logger.log("Agent " + id + " " + message);
    }
}
