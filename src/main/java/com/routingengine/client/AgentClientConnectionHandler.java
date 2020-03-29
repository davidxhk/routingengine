package com.routingengine.client;

import static com.routingengine.json.JsonUtils.getAsJsonObject;
import java.io.IOException;
import java.util.Map;
import com.google.gson.JsonObject;
import com.routingengine.Logger;
import com.routingengine.json.JsonResponse;


public class AgentClientConnectionHandler extends CustomerClientConnectionHandler
{
    public int numberOfSupportRequestsToService;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException, EndConnectionException
    {
        log("initialized!");
        
        randomSleep();
        
        log("creating new agent");
        JsonResponse response = newAgent(Map.of(clientId % 3, true));
        String agentUUIDString = getUUID(response);
        log("uuid -> " + agentUUIDString);
        
        randomSleep();
        
        log("updating availability");
        response = updateAgentAvailability(agentUUIDString, true);
        log(response);
        
        randomSleep();
        
        for (int i = 0; i < numberOfSupportRequestsToService; i++) {
            log("taking support request");
            response = takeSupportRequest(agentUUIDString);
            log(response);
            
            if (!response.didSucceed()) {
                log("exiting");
                exit();
            }
            
            String supportRequestUUIDString = getUUID(getAssignedSupportRequest(response));
        
            randomSleep();
        
            log("closing support request");
            response = closeSupportRequest(supportRequestUUIDString);
            log(response);
        }
        
        log("exiting");
        exit();
    }
    
    private final void log(String message)
    {
        Logger.log("Agent " + clientId + " " + message);
    }
    
    private static final JsonObject getAssignedSupportRequest(JsonResponse response)
    {
        return getAsJsonObject(response.getPayload(), "assigned_support_request");
    }
}
