package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;


public class RemoveAgentMethod extends CheckAgentMethod
{
  @Override
  public JsonElement handle(JsonObject arguments)
  {
    Agent agent = getAgent(arguments);
    
    removeAgent(agent);
    
    return agent.toJson();
  }
  
  public void removeAgent(Agent agent)
  {
    routingEngine.removeAgent(agent);
    
    if (agent.hasAssignedSupportRequest()) {
      agent.getAssignedSupportRequest().setAssignedAgent(null);
      agent.setAssignedSupportRequest(null);
    }
    
    agent.setInactive();
  }
}
