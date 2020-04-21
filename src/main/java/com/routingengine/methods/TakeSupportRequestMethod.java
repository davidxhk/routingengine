package com.routingengine.methods;

import java.util.concurrent.TimeoutException;
import com.routingengine.Agent;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class TakeSupportRequestMethod extends AbstractAgentMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        Agent agent = getAgent(request);
        
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
        
        return JsonResponse.success(request, agent.toJson());
    }
}
