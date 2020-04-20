package com.routingengine.methods;

import java.util.concurrent.TimeoutException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class TakeSupportRequestMethod extends AbstractAgentMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        Agent agent = getAgent(arguments);
        
        if (agent.hasAssignedSupportRequest())
            throw new IllegalStateException("agent already has assigned support request");
        
        try {
            agent.startWaiting();
            
            routingEngine.assignSupportRequest(agent);
            
            agent.stopWaiting();
        }
        
        catch (TimeoutException exception) {
            agent.stopWaiting();
            
            throw new IllegalStateException("take support request timeout");
        }
        
        catch (InterruptedException exception) {
            agent.stopWaiting();
            
            Thread.currentThread().interrupt();
            
            throw new IllegalStateException("take support request interrupted");
        }
        
        if (!agent.hasAssignedSupportRequest())
            throw new IllegalStateException("routing engine failed to assign support request");
        
        return agent.toJson();
    }
}
