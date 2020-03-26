package com.routingengine.methods;

import java.util.concurrent.TimeoutException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Logger;
import com.routingengine.SupportRequest;


public class WaitForAgentMethod extends CheckSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        waitForAgent(supportRequest);
        
        return supportRequest.toJson();
    }
    
    public void waitForAgent(SupportRequest supportRequest)
    {
        if (!supportRequest.isOpen())
            throw new IllegalStateException("support request closed");
        
        if (supportRequest.isWaiting())
            throw new IllegalStateException("support request already waiting");
        
        if (supportRequest.hasAssignedAgent())
            throw new IllegalStateException("support request has assigned agent");
        
        if (!routingEngine.isQueued(supportRequest))
            routingEngine.putInQueue(supportRequest);
        
        while (!supportRequest.hasAssignedAgent()) {
            try {
                Logger.log("Started waiting...");
                
                supportRequest.startWaiting();
                
                Logger.log("Stopped waiting");
            }
            
            catch (TimeoutException exception) {
                supportRequest.incrementPriority();
                Logger.log("Waiting for agent timeout. Priority increased to " + supportRequest.getPriority());
                
                routingEngine.replaceIntoQueue(supportRequest);
            }
            
            catch (InterruptedException exception) {
                routingEngine.removeFromQueue(supportRequest);
                
                if (supportRequest.isWaiting())
                    supportRequest.stopWaiting();
                
                Thread.currentThread().interrupt();
                
                throw new IllegalStateException("wait for agent interrupted");
            }
        }
    }
}
