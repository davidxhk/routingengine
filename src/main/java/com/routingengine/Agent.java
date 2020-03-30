package com.routingengine;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.getAsBoolean;
import static com.routingengine.json.JsonUtils.getAsStringList;
import static com.routingengine.json.JsonUtils.getAsJsonObject;
import static com.routingengine.SupportRequest.Type;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Agent extends InetEntity
{
    volatile Map<Type, Boolean> skills;
    volatile boolean activated;
    volatile boolean available;
    volatile boolean waiting;
    volatile SupportRequest assignedSupportRequest;
    
    private Agent()
    {
        super();
    }
    
    private Agent(String address)
    {
        super();
        
        super.setAddress(address);
    }
    
    private Agent(String uuid, String address)
    {
        super(uuid, address);
    }
    
    private void initialize(AgentBuilder builder)
    {
        skills = new HashMap<>();
        for (Type requestType : Type.values())
            skills.put(requestType, false);
        
        setSkills(builder.skills);
        
        activated = true;
        available = false;
        waiting = false;
        assignedSupportRequest = null;
    }
    
    public synchronized boolean ableToService(Type requestType)
    {
        if (!activated)
            return false;
        
        return skills.get(requestType);
    }
    
    public synchronized Type[] getSkills()
    {
        return skills.entrySet().stream()
                .filter(entry -> entry.getValue())
                .map(entry -> entry.getKey())
                .toArray(Type[]::new);
    }
    
    public synchronized void setSkill(int requestTypeIndex, Boolean ableToService)
    {
        setSkill(Type.of(requestTypeIndex), ableToService);
    }
    
    public synchronized void setSkill(String requestTypeString, Boolean ableToService)
    {
        setSkill(Type.of(requestTypeString), ableToService);
    }
    
    public synchronized void setSkill(Type requestType, Boolean ableToService)
    {
        if (requestType == null)
            throw new IllegalArgumentException("skill missing");
        
        if (ableToService == null)
            throw new IllegalArgumentException("skill ability missing");
        
        skills.put(requestType, ableToService);
    }
    
    public synchronized void setSkills(Map<String, Boolean> skills)
    {
        if (skills == null)
            throw new IllegalArgumentException("skills missing");
        
        skills.entrySet().stream()
            .filter(entry -> Type.is(entry.getKey()))
            .forEach(entry -> setSkill(entry.getKey(), entry.getValue()));
        
        ensureAtLeastOneSkill();
    }
    
    private synchronized void ensureAtLeastOneSkill()
    {
        for (boolean ableToService : skills.values()) {
            if (ableToService)
                return;
        }
        
        throw new IllegalArgumentException("skill missing");
    }
    
    public synchronized boolean isActivated()
    {
        return activated;
    }
    
    public synchronized void activate()
    {
        if (activated)
            throw new IllegalStateException("agent already activated");
        
        activated = true;
    }
    
    public synchronized void deactivate()
    {
        if (!activated)
            throw new IllegalStateException("agent already deactivated");
        
        if (hasAssignedSupportRequest())
            dropAssignedSupportRequest();
        
        waiting = false;
        available = false;
        activated = false;
    }
    
    public synchronized boolean isAvailable()
    {
        return available;
    }
    
    public synchronized void setAvailability(Boolean isAvailable)
    {
        if (isAvailable == null)
            throw new IllegalArgumentException("available missing");
        
        if (!activated)
            throw new IllegalStateException("agent not activated");
        
        if (isAvailable) {
            if (waiting)
                throw new IllegalStateException("agent is waiting");
        
            if (hasAssignedSupportRequest())
                throw new IllegalStateException("agent has assigned support request");
        }
        
        available = isAvailable;
    }
    
    public synchronized boolean isWaiting()
    {
        return waiting;
    }
    
    public synchronized void startWaiting()
    {
        if (!activated)
            throw new IllegalStateException("agent not activated");
        
        if (waiting)
            throw new IllegalStateException("agent already waiting");
        
        if (!available)
            throw new IllegalStateException("agent not available");
        
        waiting = true;
        available = false;
    }
    
    public synchronized void stopWaiting()
    {
        if (!activated)
            throw new IllegalStateException("agent not activated");
        
        if (!waiting)
            throw new IllegalStateException("agent not waiting");
        
        waiting = false;
        
        if (assignedSupportRequest == null)
            available = true;
    }
    
    public synchronized SupportRequest getAssignedSupportRequest()
    {
        return assignedSupportRequest;
    }
    
    public synchronized boolean hasAssignedSupportRequest()
    { 
        return assignedSupportRequest != null;
    }
    
    public synchronized void setAssignedSupportRequest(SupportRequest supportRequest)
    {
        if (supportRequest == null)
            throw new IllegalArgumentException("support request missing");
        
        if (!activated)
            throw new IllegalStateException("agent not activated");
        
        if (!waiting && !available)
            throw new IllegalStateException("agent not available");
        
        if (assignedSupportRequest != null)
            throw new IllegalStateException("agent already has assigned support request");
        
        synchronized(supportRequest) {
            
            if (supportRequest.assignedAgent != null)
                throw new IllegalArgumentException("support request has assigned agent");
            
            if (!supportRequest.open)
                throw new IllegalArgumentException("support request is closed");
            
            if (!skills.get(supportRequest.type))
                throw new IllegalArgumentException("support request unable to be serviced");
            
            supportRequest.assignedAgent = this;
            
            assignedSupportRequest = supportRequest;
            
            available = false;
        }
    }
    
    public synchronized void dropAssignedSupportRequest()
    {
        if (assignedSupportRequest == null)
            throw new IllegalStateException("agent has no assigned support request");
        
        synchronized(assignedSupportRequest) {
            
            if (assignedSupportRequest.assignedAgent == this) {
                assignedSupportRequest.assignedAgent = null;
                
                assignedSupportRequest.priority *= 2;
            }
        }
        
        assignedSupportRequest = null;
        
        if (activated)
            available = true;
    }
    
    public synchronized JsonObject toJson()
    {
        JsonObject agentJsonObject = new JsonObject();
        
        agentJsonObject.addProperty("uuid", getUUID().toString());
        
        agentJsonObject.addProperty("address", getAddress().getHostAddress());
        
        agentJsonObject.add("skills", new Gson().toJsonTree(getSkills()).getAsJsonArray());
        
        agentJsonObject.addProperty("activated", activated);
        
        agentJsonObject.addProperty("available", available);
        
        agentJsonObject.addProperty("waiting", waiting);
        
        JsonObject assignedSupportRequestJsonObject = null;
        
        if (hasAssignedSupportRequest()) {
            assignedSupportRequestJsonObject = new JsonObject();
            
            assignedSupportRequestJsonObject.addProperty("uuid", assignedSupportRequest.getUUID().toString());
            
            assignedSupportRequestJsonObject.addProperty("address", assignedSupportRequest.getAddress().getHostAddress());
            
            assignedSupportRequestJsonObject.add("user", assignedSupportRequest.user.toJson());
            
            assignedSupportRequestJsonObject.addProperty("type", assignedSupportRequest.type.toString());
            
            assignedSupportRequestJsonObject.addProperty("priority", assignedSupportRequest.priority);
        }
        
        agentJsonObject.add("assigned_support_request", assignedSupportRequestJsonObject);
        
        return agentJsonObject;
    }
    
    public static Agent fromJson(JsonObject jsonObject)
    {
        Map<String, Boolean> skills = new HashMap<>();
        for (String skill : getAsStringList(jsonObject, "skills"))
            skills.put(skill, true);
        
        Agent agent = builder()
            .setUUID(getAsString(jsonObject, "uuid"))
            .setAddress(getAsString(jsonObject, "address"))
            .setSkills(skills)
            .build();
        
        if (jsonObject.has("activated"))
            agent.activated = getAsBoolean(jsonObject, "activated");
        
        if (jsonObject.has("available"))
            agent.available = getAsBoolean(jsonObject, "available");
        
        if (jsonObject.has("assigned_support_request")) {
            JsonObject supportRequestJson = getAsJsonObject(jsonObject, "assigned_support_request");
            
            if (supportRequestJson != null)
                agent.assignedSupportRequest = SupportRequest.fromJson(supportRequestJson);
        }
        
        return agent;
    }
    
    public static AgentBuilder builder()
    {
        return new AgentBuilder();
    }
    
    public static class AgentBuilder
    {
        private String uuid;
        private String address;
        private Map<String, Boolean> skills;
        
        public AgentBuilder()
        {
            uuid = null;
            address = null;
            skills = new HashMap<>();
        }
        
        public AgentBuilder setUUID(String UUIDString)
        {
            uuid = UUIDString;
            
            return this;
        }
        
        public AgentBuilder setAddress(String addressString)
        {
            address = addressString;
            
            return this;   
        }
        
        public AgentBuilder setSkill(int requestTypeIndex, boolean ableToService)
        {
            skills.put(String.valueOf(requestTypeIndex), ableToService);
            
            return this;
        }
        
        public AgentBuilder setSkill(String requestTypeString, boolean ableToService)
        {
            skills.put(requestTypeString, ableToService);
            
            return this;
        }
        
        public AgentBuilder setSkill(Type requestType, boolean ableToService)
        {
            skills.put(requestType.toString(), ableToService);
            
            return this;
        }
        
        public AgentBuilder setSkills(Map<String, Boolean> skills)
        {
            if (skills == null)
                this.skills = null;
            
            else
                this.skills.putAll(skills);
            
            return this;
        }
        
        public Agent build()
        {
            Agent agent;
            
            if (uuid != null)
                agent = new Agent(uuid, address);
            
            else if (address != null)
                agent = new Agent(address);
            
            else
                agent = new Agent();
            
            agent.initialize(this);
            
            return agent;
        }
    }
}
