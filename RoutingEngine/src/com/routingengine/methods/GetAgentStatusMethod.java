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
    
    JsonArray activeAgentsArray = new JsonArray();
    int activeAgentCount = 0;
    
    JsonArray availableAgentsArray = new JsonArray();
    int availableAgentCount = 0;
    
    JsonArray assignedAgentsArray = new JsonArray();
    int assignedAgentCount = 0;
    
    for (Agent agent : agents) {
      String agentUUIDString = agent.getUUID().toString();
      
      totalAgentCount++;
      
      if (agent.isActive()) {
        activeAgentsArray.add(agentUUIDString);
        activeAgentCount++;
        
        if (agent.isAvailable()) {
          availableAgentsArray.add(agentUUIDString);
          availableAgentCount++;
        }
        
        else if (agent.hasAssignedSupportRequest()) {
          assignedAgentsArray.add(agentUUIDString);
          assignedAgentCount++;
        }
      }  
    }
    
    agentStatus.addProperty("total", totalAgentCount);
    
    JsonObject activeAgentsStatus = new JsonObject();
    activeAgentsStatus.addProperty("count", activeAgentCount);
    activeAgentsStatus.add("uuids", activeAgentsArray);
    agentStatus.add("active", activeAgentsStatus);
    
    JsonObject availableAgentsStatus = new JsonObject();
    availableAgentsStatus.addProperty("count", availableAgentCount);
    availableAgentsStatus.add("uuids", availableAgentsArray);
    agentStatus.add("available", availableAgentsStatus);
    
    JsonObject assignedAgentsStatus = new JsonObject();
    assignedAgentsStatus.addProperty("count", assignedAgentCount);
    assignedAgentsStatus.add("uuids", assignedAgentsArray);
    agentStatus.add("assigned", assignedAgentsStatus);
    
    return agentStatus;
  }
}
