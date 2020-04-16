package com.routingengine;

import static com.routingengine.RequestQueueManager.RequestQueue;
import static com.routingengine.json.JsonConnectionHandler.SLEEP_MILLIS;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.routingengine.SupportRequest.Type;


public class RoutingEngine
{
    private ConcurrentHashMap<UUID, SupportRequest> supportRequests;
    private ConcurrentHashMap<UUID, Agent> agents;
    private RequestQueueManager requestQueueManager;
    public static final long TIMEOUT_MILLIS = 30000L;
    
    public RoutingEngine()
    {
        supportRequests = new ConcurrentHashMap<>();
        agents = new ConcurrentHashMap<>();
        requestQueueManager = new RequestQueueManager();
    }
    
    public void addSupportRequest(SupportRequest supportRequest)
    {
        if (supportRequest == null)
            throw new IllegalArgumentException("support request missing");
        
        supportRequests.put(supportRequest.getUUID(), supportRequest);
    }
    
    public SupportRequest getSupportRequest(String supportRequestUUIDString)
    {
        if (supportRequestUUIDString == null)
            throw new IllegalArgumentException("uuid missing");
        
        UUID supportRequestUUID = null;
        
        try {
            supportRequestUUID = UUID.fromString(supportRequestUUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
        
        if (supportRequestUUID == null || !supportRequests.containsKey(supportRequestUUID))
            throw new IllegalArgumentException("uuid not found");
        
        return supportRequests.get(supportRequestUUID);
    }
    
    public SupportRequest[] getSupportRequests()
    {
        return supportRequests.values().toArray(SupportRequest[]::new);
    }
    
    public SupportRequest removeSupportRequest(String supportRequestUUIDString)
    {
        if (supportRequestUUIDString == null)
            throw new IllegalArgumentException("uuid missing");
        
        UUID supportRequestUUID = null;
        
        try {
            supportRequestUUID = UUID.fromString(supportRequestUUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
        
        if (supportRequestUUID == null || !supportRequests.containsKey(supportRequestUUID))
            throw new IllegalArgumentException("uuid not found");
        
        SupportRequest supportRequest = supportRequests.get(supportRequestUUID);
        
        supportRequests.remove(supportRequestUUID);
        
        return supportRequest;
    }
    
    public void addAgent(Agent agent)
    {
        if (agent == null)
            throw new IllegalArgumentException("missing agent");
        
        agents.put(agent.getUUID(), agent);
    }
    
    public Agent getAgent(String agentUUIDString)
    {
        if (agentUUIDString == null)
            throw new IllegalArgumentException("uuid missing");
        
        UUID agentUUID = null;
        
        try {
            agentUUID = UUID.fromString(agentUUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
        
        if (agentUUID == null || !agents.containsKey(agentUUID))
            throw new IllegalArgumentException("uuid not found");
        
        return agents.get(agentUUID);
    }
    
    public Agent[] getAgents()
    {
        return agents.values().toArray(Agent[]::new);
    }
    
    public Agent removeAgent(String agentUUIDString)
    {
        if (agentUUIDString == null)
            throw new IllegalArgumentException("uuid missing");
        
        UUID agentUUID = null;
        
        try {
            agentUUID = UUID.fromString(agentUUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
        
        if (agentUUID == null || !agents.containsKey(agentUUID))
            throw new IllegalArgumentException("uuid not found");
        
        Agent agent = agents.get(agentUUID);
        
        agents.remove(agentUUID);
        
        return agent;
    }
    
    public void assignAgent(SupportRequest supportRequest)
        throws InterruptedException, TimeoutException
    {
        selectQueue(supportRequest).put(supportRequest);
        
        long start = System.currentTimeMillis();
        
        while (!supportRequest.hasAssignedAgent()) {
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
            }
            
            catch (InterruptedException exception) {
                selectQueue(supportRequest).remove(supportRequest);
                
                Thread.currentThread().interrupt();
                
                throw exception;
            }
            
            if (System.currentTimeMillis() - start >= TIMEOUT_MILLIS) {
                selectQueue(supportRequest).remove(supportRequest);
                
                throw new TimeoutException();
            }
            
            if (!supportRequest.isWaiting()) {
                selectQueue(supportRequest).remove(supportRequest);
                
                return;
            }
        }
    }
    
    public void assignSupportRequest(Agent agent)
        throws InterruptedException, TimeoutException
    {
        SupportRequest assignedSupportRequest = selectQueue(agent).take();
        
        long start = System.currentTimeMillis();
        
        while (assignedSupportRequest == null) {
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
            
            if (System.currentTimeMillis() - start >= TIMEOUT_MILLIS)
                throw new TimeoutException();
            
            if (!agent.isWaiting())
                return;
            
            assignedSupportRequest = selectQueue(agent).take();
        }
        
        agent.setAssignedSupportRequest(assignedSupportRequest);
    }
    
    public int getQueueCount(Type requestType)
    {
        return requestQueueManager.getQueue(requestType).getCount();
    }
    
    public SupportRequest[] getQueuedSupportRequests(Type requestType)
    {
        return requestQueueManager.getQueue(requestType).toArray();
    }
    
    private RequestQueue selectQueue(SupportRequest supportRequest)
    {
        return requestQueueManager.getQueue(supportRequest.getType());
    }
    
    private RequestQueue selectQueue(Agent agent)
    {
        int maxCount = Integer.MIN_VALUE;
        
        RequestQueue queue, maxQueue = null;
        
        for (Type requestType : agent.getSkills()) {
            queue = requestQueueManager.getQueue(requestType);
            
            if (maxQueue == null || queue.getCount() > maxCount) {
                maxQueue = queue;
                maxCount = queue.getCount();
            }
        }
        
        return maxQueue;
    }
}
