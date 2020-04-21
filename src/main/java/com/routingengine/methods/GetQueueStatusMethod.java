package com.routingengine.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class GetQueueStatusMethod extends AbstractAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        JsonObject queueStatus = new JsonObject();
        
        for (SupportRequest.Type requestType : SupportRequest.Type.values()) {
            
            JsonObject subQueueStatus = new JsonObject();
            
            subQueueStatus.addProperty("count", routingEngine.getQueueCount(requestType));
            
            JsonArray queuedSupportRequestsArray = new JsonArray();
            
            for (SupportRequest supportRequest : routingEngine.getQueuedSupportRequests(requestType))
                queuedSupportRequestsArray.add(supportRequest.getUUID().toString());
            
            subQueueStatus.add("uuids", queuedSupportRequestsArray);
            
            queueStatus.add(requestType.toString(), subQueueStatus);
        }
        
        return JsonResponse.success(request, queueStatus);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return true;
    }
}
