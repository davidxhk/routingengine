package com.routingengine.methods;

import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class GetQueueStatusMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonObject payload = getQueueStatus();
        
        return payload;
    }
    
    public JsonObject getQueueStatus()
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
        
        return queueStatus;
    }
}
