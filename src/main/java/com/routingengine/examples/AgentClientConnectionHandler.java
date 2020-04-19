package com.routingengine.examples;

import static com.routingengine.json.JsonUtils.castToString;
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
        throws IOException, InterruptedException
    {
        log("initialized!");
        
        randomSleep();
        
        log("creating new agent");
        newAgent(Map.of(type, true));
        JsonResponse response = awaitResponse();
        
        String agentUUIDString = getUUID(response);
        log("uuid -> " + agentUUIDString);
        
        randomSleep();
        
        log("updating availability");
        updateAgentAvailability(agentUUIDString, true);
        awaitResponse();
        
        randomSleep();
        
        for (int i = 0; i < numberOfSupportRequestsToService; i++) {
            log("taking support request");
            takeSupportRequest(agentUUIDString);
            response = awaitResponse();
            
            if (!response.didSucceed()) {
                String error = castToString(response.getPayload());
                
                if (!"take support request timeout".matches(error)) {
                    log("got unexpected error -> "+ error);
                    
                    log("exiting");
                    exit();
                }
            }
            
            String supportRequestUUIDString = getUUID(getAssignedSupportRequest(response));
            
            randomSleep();
            
            log("closing support request");
            closeSupportRequest(supportRequestUUIDString);
            awaitResponse();
        }
        
        log("exiting");
        exit();
    }
    
    @Override
    protected void log(String message)
    {
        Logger.log("Agent " + id + " " + message);
    }
    
    private static final JsonObject getAssignedSupportRequest(JsonResponse response)
    {
        return getAsJsonObject(response.getPayload(), "assigned_support_request");
    }
}
