package com.routingengine.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.MethodManager;
import com.routingengine.SupportRequest;


public class GetSupportRequestStatusMethod extends MethodManager.Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonObject payload = getSupportRequestStatus();
        
        return payload;
    }
    
    public JsonObject getSupportRequestStatus()
    {
        SupportRequest[] supportRequests = routingEngine.getSupportRequests();
        
        JsonObject supportRequestStatus = new JsonObject();
        int totalSupportRequestCount = 0;
        
        JsonArray openSupportRequestsArray = new JsonArray();
        int openSupportRequestCount = 0;
        
        JsonArray waitingSupportRequestsArray = new JsonArray();
        int waitingSupportRequestCount = 0;
        
        JsonArray assignedSupportRequestsArray = new JsonArray();
        int assignedSupportRequestCount = 0;
        
        for (SupportRequest supportRequest : supportRequests) {
            String supportRequestUUIDString = supportRequest.getUUID().toString();
            
            totalSupportRequestCount++;
            
            if (supportRequest.isOpen()) {
                openSupportRequestsArray.add(supportRequestUUIDString);
                openSupportRequestCount++;
                
                if (supportRequest.isWaiting()) {
                    waitingSupportRequestsArray.add(supportRequestUUIDString);
                    waitingSupportRequestCount++;
                }
                
                else if (supportRequest.hasAssignedAgent()) {
                    assignedSupportRequestsArray.add(supportRequestUUIDString);
                    assignedSupportRequestCount++;
                }
            }
        }
        
        supportRequestStatus.addProperty("total", totalSupportRequestCount);
        
        JsonObject openSupportRequestsStatus = new JsonObject();
        openSupportRequestsStatus.addProperty("count", openSupportRequestCount);
        openSupportRequestsStatus.add("uuids", openSupportRequestsArray);
        supportRequestStatus.add("open", openSupportRequestsStatus);
        
        JsonObject waitingSupportRequestsStatus = new JsonObject();
        waitingSupportRequestsStatus.addProperty("count", waitingSupportRequestCount);
        waitingSupportRequestsStatus.add("uuids", waitingSupportRequestsArray);
        supportRequestStatus.add("waiting", waitingSupportRequestsStatus);
        
        JsonObject assignedSupportRequestsStatus = new JsonObject();
        assignedSupportRequestsStatus.addProperty("count", assignedSupportRequestCount);
        assignedSupportRequestsStatus.add("uuids", assignedSupportRequestsArray);
        supportRequestStatus.add("assigned", assignedSupportRequestsStatus);
        
        return supportRequestStatus;
    }
}
