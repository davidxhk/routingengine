package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;


public class GetStatusOverviewMethod extends AbstractAdminMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonObject status = new JsonObject();
        
        Agent[] agents = routingEngine.getAgents();
        
        int totalAgentCount = 0;
        int inactiveAgentCount = 0;
        int unavailableAgentCount = 0;
        int availableAgentCount = 0;
        int waitingAgentCount = 0;
        int assignedAgentCount = 0;
        
        for (Agent agent : agents) {
            totalAgentCount++;
            
            if (!agent.isActivated())
                inactiveAgentCount++;
            
            else if (agent.isWaiting())
                waitingAgentCount++;
            
            else if (agent.hasAssignedSupportRequest())
                assignedAgentCount++;
            
            else if (agent.isAvailable())
                availableAgentCount++;
            
            else
                unavailableAgentCount++;
        }
        
        SupportRequest[] supportRequests = routingEngine.getSupportRequests();
        
        int totalSupportRequestCount = 0;
        int closedSupportRequestCount = 0;
        int openSupportRequestCount = 0;
        int waitingSupportRequestCount = 0;
        int assignedSupportRequestCount = 0;
        
        for (SupportRequest supportRequest : supportRequests) {
            totalSupportRequestCount++;
            
            if (!supportRequest.isOpen())
                closedSupportRequestCount++;
            
            else if (supportRequest.isWaiting())
                waitingSupportRequestCount++;
            
            else if (supportRequest.hasAssignedAgent())
                assignedSupportRequestCount++;
            
            else
                openSupportRequestCount++;
        }
        
        JsonObject agentStatus = new JsonObject();
        agentStatus.addProperty("inactive", inactiveAgentCount);
        agentStatus.addProperty("unavailable", unavailableAgentCount);
        agentStatus.addProperty("available", availableAgentCount);
        agentStatus.addProperty("waiting", waitingAgentCount);
        agentStatus.addProperty("assigned", assignedAgentCount);
        agentStatus.addProperty("total", totalAgentCount);
        status.add("agent", agentStatus);
        
        JsonObject supportRequestStatus = new JsonObject();
        supportRequestStatus.addProperty("closed", closedSupportRequestCount);
        supportRequestStatus.addProperty("open", openSupportRequestCount);
        supportRequestStatus.addProperty("waiting", waitingSupportRequestCount);
        supportRequestStatus.addProperty("assigned", assignedSupportRequestCount);
        supportRequestStatus.addProperty("total", totalSupportRequestCount);
        status.add("support_request", supportRequestStatus);
        
        JsonObject queueStatus = new JsonObject();
        for (SupportRequest.Type requestType : SupportRequest.Type.values())
            queueStatus.addProperty(requestType.toString(),
                    routingEngine.getQueueCount(requestType));
        status.add("queue", queueStatus);
        
        return status;
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return true;
    }
}
