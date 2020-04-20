package com.routingengine;

import static com.routingengine.RequestQueueManager.RequestQueue;
import static com.routingengine.json.JsonConnectionHandler.SLEEP_MILLIS;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.routingengine.SupportRequest.Type;


public class RoutingEngine
{
    private ConcurrentHashMap<UUID, SupportRequest> supportRequests;
    private ConcurrentHashMap<UUID, Agent> agents;
    private Set<UUID> admins;
    private RequestQueueManager requestQueueManager;
    public static final String DEFAULT_ADMIN = "ee462c1a-ba44-45c5-a4f7-f6eb7099d82a";
    public static final long TIMEOUT_MILLIS = 30000L;
    
    public RoutingEngine()
    {
        supportRequests = new ConcurrentHashMap<>();
        agents = new ConcurrentHashMap<>();
        admins = ConcurrentHashMap.newKeySet();
        requestQueueManager = new RequestQueueManager();
        
        initialize();
    }
    
    public void initialize()
    {
        admins.add(UUID.fromString(DEFAULT_ADMIN));
    }
    
    public static String newUUID()
    {
        UUID newAdminUUID = UUID.randomUUID();
        
        return newAdminUUID.toString();
    }
    
    public static UUID convertToUUID(String UUIDString)
    {
        if (UUIDString == null)
            throw new IllegalArgumentException("uuid missing");
        
        try {
            return UUID.fromString(UUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("uuid invalid");
        }
    }
    
    public void addSupportRequest(SupportRequest supportRequest)
    {
        if (supportRequest == null)
            throw new IllegalArgumentException("support request missing");
        
        try {
            getSupportRequest(supportRequest.getUUID());
            
            throw new IllegalArgumentException("uuid already exists");
        }
        
        catch (IllegalArgumentException exception) {
            
            if (!"uuid not found".equals(exception.getMessage()))
                throw exception;
        }
        
        supportRequests.put(supportRequest.getUUID(), supportRequest);
    }
    
    public SupportRequest getSupportRequest(UUID supportRequestUUID)
    {
        if (supportRequestUUID == null)
            throw new IllegalArgumentException("uuid missing");
        
        if (!supportRequests.containsKey(supportRequestUUID))
            throw new IllegalArgumentException("uuid not found");
        
        return supportRequests.get(supportRequestUUID);
    }
    
    public SupportRequest getSupportRequest(String supportRequestUUIDString)
    {
        UUID supportRequestUUID = convertToUUID(supportRequestUUIDString);
        
        return getSupportRequest(supportRequestUUID);
    }
    
    public SupportRequest[] getSupportRequests()
    {
        return supportRequests.values().toArray(SupportRequest[]::new);
    }
    
    public void removeSupportRequest(UUID supportRequestUUID)
    {
        if (supportRequestUUID == null)
            throw new IllegalArgumentException("uuid missing");
        
        if (!supportRequests.containsKey(supportRequestUUID))
            throw new IllegalArgumentException("uuid not found");
        
        supportRequests.remove(supportRequestUUID);
    }
    
    public void addAgent(Agent agent)
    {
        if (agent == null)
            throw new IllegalArgumentException("agent missing");
        
        try {
            getAgent(agent.getUUID());
            
            throw new IllegalArgumentException("uuid already exists");
        }
        
        catch (IllegalArgumentException exception) {
            
            if (!"uuid not found".equals(exception.getMessage()))
                throw exception;
        }
        
        try {
            getAgentFromRainbowId(agent.getRainbowId());
            
            throw new IllegalArgumentException("rainbow id already exists");
        }
        
        catch (IllegalArgumentException exception) {
            
            if (!"rainbow id not found".equals(exception.getMessage()))
                throw exception;
        }
        
        agents.put(agent.getUUID(), agent);
    }
    
    public Agent getAgent(UUID agentUUID)
    {
        if (agentUUID == null)
            throw new IllegalArgumentException("uuid missing");
        
        if (!agents.containsKey(agentUUID))
            throw new IllegalArgumentException("uuid not found");
        
        return agents.get(agentUUID);
    }
    
    public Agent getAgent(String agentUUIDString)
    {
        UUID agentUUID = convertToUUID(agentUUIDString);
        
        return getAgent(agentUUID);
    }
    
    public Agent getAgentFromRainbowId(String rainbowIdString)
    {
        if (rainbowIdString == null)
            throw new IllegalArgumentException("rainbow id missing");
        
        for (Agent agent : getAgents()) {
            
            if (rainbowIdString.equals(agent.getRainbowId()))
                return agent;
        }
        
        throw new IllegalArgumentException("rainbow id not found");
    }
    
    public Agent[] getAgents()
    {
        return agents.values().toArray(Agent[]::new);
    }
    
    public void removeAgent(UUID agentUUID)
    {
        if (agentUUID == null)
            throw new IllegalArgumentException("uuid missing");
        
        if (!agents.containsKey(agentUUID))
            throw new IllegalArgumentException("uuid not found");
        
        agents.remove(agentUUID);
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
    
    public void addAdmin(String adminUUIDString)
    {
        UUID adminUUID = convertToUUID(adminUUIDString);
        
        admins.add(adminUUID);
    }
    
    public void verifyAdmin(String adminUUIDString)
    {
        UUID adminUUID;
        
        try {
            adminUUID = convertToUUID(adminUUIDString);
        }
        
        catch (IllegalArgumentException exception) {
            String error = "admin " + exception.getMessage();
            
            throw new IllegalArgumentException(error);
        }
        
        if (!admins.contains(adminUUID))
            throw new IllegalArgumentException("unauthorized access");
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
