package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class CloseSupportRequestMethod extends CheckSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        closeSupportRequest(supportRequest);
        
        return supportRequest.toJson();
    }
    
    public void closeSupportRequest(SupportRequest supportRequest)
    {        
        if (supportRequest.hasAssignedAgent()) {
            supportRequest.getAssignedAgent().setAssignedSupportRequest(null);
            supportRequest.setAssignedAgent(null);
        }
        
        supportRequest.close();
        
        routingEngine.removeFromQueue(supportRequest);
    }
}
