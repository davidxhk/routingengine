package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodManager;
import com.routingengine.SupportRequest;


public class GetStatusOverviewMethod extends MethodManager.Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonObject payload = getStatusOverview();
        
        return payload;
    }
    
    public JsonObject getStatusOverview()
    {
        JsonObject status = new JsonObject();
        
        Agent[] agents = routingEngine.getAgents();
        
        int totalAgentCount = 0;
        int activeAgentCount = 0;
        int availableAgentCount = 0;
        int assignedAgentCount = 0;
        
        for (Agent agent : agents) {
            totalAgentCount++;
            
            if (agent.isActive()) {
                activeAgentCount++;
                
                if (agent.isAvailable())
                    availableAgentCount++;
                
                else if (agent.hasAssignedSupportRequest())
                    assignedAgentCount++;
            }    
        }
        
        SupportRequest[] supportRequests = routingEngine.getSupportRequests();
        
        int totalSupportRequestCount = 0;
        int openSupportRequestCount = 0;
        int waitingSupportRequestCount = 0;
        int assignedSupportRequestCount = 0;
        
        for (SupportRequest supportRequest : supportRequests) {
            totalSupportRequestCount++;
            
            if (supportRequest.isOpen()) {
                openSupportRequestCount++;
                
                if (supportRequest.isWaiting())
                    waitingSupportRequestCount++;
                
                else if (supportRequest.hasAssignedAgent())
                    assignedSupportRequestCount++;
            }
        }
        
        JsonObject agentStatus = new JsonObject();
        agentStatus.addProperty("total", totalAgentCount);
        agentStatus.addProperty("active", activeAgentCount);
        agentStatus.addProperty("available", availableAgentCount);
        agentStatus.addProperty("assigned", assignedAgentCount);
        status.add("agent", agentStatus);
        
        JsonObject supportRequestStatus = new JsonObject();
        supportRequestStatus.addProperty("total", totalSupportRequestCount);
        supportRequestStatus.addProperty("open", openSupportRequestCount);
        supportRequestStatus.addProperty("waiting", waitingSupportRequestCount);
        supportRequestStatus.addProperty("assigned", assignedSupportRequestCount);
        status.add("support_request", supportRequestStatus);
        
        JsonObject queueStatus = new JsonObject();
        for (SupportRequest.Type requestType : SupportRequest.Type.values())
            queueStatus.addProperty(requestType.toString(),
                    routingEngine.getQueueCount(requestType));
        status.add("queue", queueStatus);
        
        return status;
    }
}
