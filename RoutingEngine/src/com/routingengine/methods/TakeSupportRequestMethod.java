package com.routingengine.methods;

import java.util.concurrent.TimeoutException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;
import com.routingengine.Agent;


public class TakeSupportRequestMethod extends CheckAgentMethod
{
  @Override
  public JsonElement handle(JsonObject arguments)
  {
    Agent agent = getAgent(arguments);
    
    takeSupportRequest(agent);
    
    return agent.toJson();
  }
  
  public void takeSupportRequest(Agent agent)
  {
    if (!agent.isActive())
      throw new IllegalStateException("agent not active anymore");
    
    if (!agent.isAvailable())
      throw new IllegalStateException("agent unavailable");
    
    if (agent.hasAssignedSupportRequest())
      throw new IllegalStateException("agent servicing another request");
    
    try {
      SupportRequest supportRequest = routingEngine.takeFromQueue(agent);
      
      supportRequest.setAssignedAgent(agent);
      
      agent.setAssignedSupportRequest(supportRequest);
      
      supportRequest.stopWaiting();
    }
    
    catch (TimeoutException exception) {
      throw new IllegalStateException("take support request timeout");
    }
    
    catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      
      throw new IllegalStateException("take support request interrupted");
    }
  }
}
