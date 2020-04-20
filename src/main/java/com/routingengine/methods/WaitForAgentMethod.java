package com.routingengine.methods;

import java.util.concurrent.TimeoutException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class WaitForAgentMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        if (supportRequest.hasAssignedAgent())
            throw new IllegalStateException("support request already has assigned agent");
        
        try {
            supportRequest.startWaiting();
            
            routingEngine.assignAgent(supportRequest);
            
            supportRequest.stopWaiting();
        }
        
        catch (TimeoutException exception) {
            supportRequest.stopWaiting();
            
            supportRequest.incrementPriority();
            
            throw new IllegalStateException("wait for agent timeout");
        }
        
        catch (InterruptedException exception) {
            supportRequest.stopWaiting();
            
            supportRequest.incrementPriority();
            
            Thread.currentThread().interrupt();
            
            throw new IllegalStateException("wait for agent interrupted");
        }
        
        if (!supportRequest.hasAssignedAgent()) {
            supportRequest.incrementPriority();
            
            throw new IllegalStateException("routing engine failed to assign agent");
        }
        
        return supportRequest.toJson();
    }
}
