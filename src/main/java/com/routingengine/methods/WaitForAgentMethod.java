package com.routingengine.methods;

import java.util.concurrent.TimeoutException;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class WaitForAgentMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest supportRequest = getSupportRequest(request);
        
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
        
        return JsonResponse.success(request, supportRequest.toJson());
    }
}
