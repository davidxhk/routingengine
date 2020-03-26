package com.routingengine.methods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodManager;


public class GetAgentStatusMethod extends MethodManager.Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        JsonObject payload = getAgentStatus();
        
        return payload;
    }
    
    public JsonObject getAgentStatus()
    {
        Agent[] agents = routingEngine.getAgents();
        
        JsonObject agentStatus = new JsonObject();
        int totalAgentCount = 0;
        
        JsonArray inactiveAgentsArray = new JsonArray();
        int inactiveAgentCount = 0;
        
        JsonArray availableAgentsArray = new JsonArray();
        int availableAgentCount = 0;
        
        JsonArray waitingAgentsArray = new JsonArray();
        int waitingAgentCount = 0;
        
        JsonArray assignedAgentsArray = new JsonArray();
        int assignedAgentCount = 0;
        
        for (Agent agent : agents) {
            String agentUUIDString = agent.getUUID().toString();
            
            totalAgentCount++;
            
            if (!agent.isActivated()) {
                inactiveAgentsArray.add(agentUUIDString);
                inactiveAgentCount++;
            }
            
            else if (agent.isWaiting()) {
                waitingAgentsArray.add(agentUUIDString);
                waitingAgentCount++;
            }
            
            else if (agent.hasAssignedSupportRequest()) {
                assignedAgentsArray.add(agentUUIDString);
                assignedAgentCount++;
            }
            
            else {
                availableAgentsArray.add(agentUUIDString);
                availableAgentCount++;
            }
        }
        
        JsonObject activeAgentsStatus = new JsonObject();
        activeAgentsStatus.addProperty("count", inactiveAgentCount);
        activeAgentsStatus.add("uuids", inactiveAgentsArray);
        agentStatus.add("inactive", activeAgentsStatus);
        
        JsonObject availableAgentsStatus = new JsonObject();
        availableAgentsStatus.addProperty("count", availableAgentCount);
        availableAgentsStatus.add("uuids", availableAgentsArray);
        agentStatus.add("available", availableAgentsStatus);
        
        JsonObject waitingAgentsStatus = new JsonObject();
        waitingAgentsStatus.addProperty("count", waitingAgentCount);
        waitingAgentsStatus.add("uuids", waitingAgentsArray);
        agentStatus.add("waiting", waitingAgentsStatus);
        
        JsonObject assignedAgentsStatus = new JsonObject();
        assignedAgentsStatus.addProperty("count", assignedAgentCount);
        assignedAgentsStatus.add("uuids", assignedAgentsArray);
        agentStatus.add("assigned", assignedAgentsStatus);
        
        agentStatus.addProperty("total", totalAgentCount);
        
        return agentStatus;
    }
}
