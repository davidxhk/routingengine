package com.routingengine;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;


public class RoutingEngine
{
  private ConcurrentHashMap<UUID, SupportRequest> supportRequestList;
  private ConcurrentHashMap<UUID, Agent> agentList;
  private RequestQueueManager requestQueueManager;
  
  public RoutingEngine()
  {
    supportRequestList = new ConcurrentHashMap<>();
    agentList = new ConcurrentHashMap<>();
    requestQueueManager = new RequestQueueManager();
  }
  
  public RequestQueueManager getRequestQueueManager()
  {
    return requestQueueManager;
  }
  
  public void addSupportRequest(SupportRequest supportRequest)
  {
    supportRequestList.put(supportRequest.getUUID(), supportRequest);
  }
  
  public SupportRequest getSupportRequest(String supportRequestUUIDString)
  {
    if (supportRequestUUIDString == null)
      throw new IllegalArgumentException("uuid missing");
    
    UUID supportRequestUUID;
    
    try {
      supportRequestUUID = UUID.fromString(supportRequestUUIDString);
    }
    
    catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("uuid invalid");
    }
    
    SupportRequest supportRequest = supportRequestList.get(supportRequestUUID);
    
    if (supportRequest == null)
      throw new IllegalArgumentException("uuid not found");
    
    return supportRequest;
  }
  
  public SupportRequest[] getSupportRequests()
  {
    return supportRequestList.values().toArray(SupportRequest[]::new);
  }
  
  public void removeSupportRequest(SupportRequest supportRequest)
  {
    supportRequestList.remove(supportRequest.getUUID());
  }
  
  public void addAgent(Agent agent)
  {
    agentList.put(agent.getUUID(), agent);
  }
  
  public Agent getAgent(String agentUUIDString)
  {
    if (agentUUIDString == null)
      throw new IllegalArgumentException("uuid missing");
    
    UUID agentUUID;
    
    try {
      agentUUID = UUID.fromString(agentUUIDString);
    }
    
    catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("uuid invalid");
    }
    
    Agent agent = agentList.get(agentUUID);
    
    if (agent == null)
      throw new IllegalArgumentException("uuid not found");
    
    return agent;
  }
  
  public Agent[] getAgents()
  {
    return agentList.values().toArray(Agent[]::new);
  }
  
  public void removeAgent(Agent agent)
  {
    agentList.remove(agent.getUUID());
  }
  
  public void putInQueue(SupportRequest supportRequest)
  {
    requestQueueManager.putSupportRequest(supportRequest);
  }
  
  public SupportRequest takeFromQueue(Agent agent)
    throws InterruptedException, TimeoutException
  {
    return requestQueueManager.takeSupportRequest(agent);
  }

  public void removeFromQueue(SupportRequest supportRequest)
  {
    requestQueueManager.removeSupportRequest(supportRequest);
  }
  
  public void replaceIntoQueue(SupportRequest supportRequest)
  {
    removeFromQueue(supportRequest);
    putInQueue(supportRequest);
  }
  
  public boolean isQueued(SupportRequest supportRequest)
  {
    return requestQueueManager.isQueued(supportRequest);
  }
  
  public int getQueueCount(SupportRequest.Type requestType)
  {
    return requestQueueManager.getQueueCount(requestType);
  }
  
  public SupportRequest[] getQueuedSupportRequests(SupportRequest.Type requestType)
  {
    return requestQueueManager.getQueuedSupportRequests(requestType);
  }
}
