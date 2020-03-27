package com.routingengine;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.getAsBooleanMap;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Agent extends InetEntity
{
    private volatile HashMap<SupportRequest.Type, Boolean> skills;
    private volatile boolean activated;
    private volatile boolean available;
    private volatile boolean waiting;
    private volatile SupportRequest assignedSupportRequest;
    
    private Agent(AgentBuilder builder)
    {
        super(builder.address);
        
        skills = new HashMap<>();
        for (SupportRequest.Type requestType : SupportRequest.Type.values())
            skills.put(requestType, false);
        
        setSkills(builder.skills);
        
        activated = true;
        available = false;
        waiting = false;
        assignedSupportRequest = null;
    }
    
    public synchronized boolean ableToService(SupportRequest.Type requestType)
    {
        if (!activated)
            return false;
        
        return skills.get(requestType);
    }
    
    public synchronized SupportRequest.Type[] getSkills()
    {
        return skills.entrySet().stream()
                .filter(entry -> entry.getValue())
                .map(entry -> entry.getKey())
                .toArray(SupportRequest.Type[]::new);
    }
    
    public synchronized void setSkill(int requestTypeIndex, Boolean ableToService)
    {
        setSkill(SupportRequest.Type.of(requestTypeIndex), ableToService);
    }
    
    public synchronized void setSkill(String requestTypeString, Boolean ableToService)
    {
        setSkill(SupportRequest.Type.of(requestTypeString), ableToService);
    }
    
    public synchronized void setSkill(SupportRequest.Type requestType, Boolean ableToService)
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
            .filter(entry -> SupportRequest.Type.is(entry.getKey()))
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
    
    public synchronized void activate()
    {
        activated = true;
    }
    
    public synchronized void deactivate()
    {
        available = false;
        activated = false;
    }
    
    public synchronized boolean isActivated()
    {
        return activated;
    }
    
    public synchronized void setAvailability(Boolean isAvailable)
    {
        if (isAvailable == null)
            throw new IllegalArgumentException("available missing");
        
        if (!activated)
            return;
        
        available = isAvailable;
    }
    
    public synchronized boolean isAvailable()
    {
        return available;
    }
    
    public synchronized void startWaiting()
    {
        if (!activated)
            return;
        
        waiting = true;
        available = false;
    }
    
    public synchronized void stopWaiting()
    {
        if (!activated)
            return;
        
        waiting = false;
        available = true;
    }
    
    public synchronized boolean isWaiting()
    {
        return waiting;
    }
    
    public synchronized SupportRequest getAssignedSupportRequest()
    {
        return assignedSupportRequest;
    }
    
    public synchronized void setAssignedSupportRequest(SupportRequest supportRequest)
    {
        if (!activated)
            return;
        
        assignedSupportRequest = supportRequest;
        
        available = !hasAssignedSupportRequest();
    }
    
    public synchronized boolean hasAssignedSupportRequest()
    { 
        return assignedSupportRequest != null;
    }
    
    public synchronized JsonObject toJson()
    {
        JsonObject agentJsonObject = new JsonObject();
        
        agentJsonObject.addProperty("uuid", getUUID().toString());
        
        agentJsonObject.addProperty("address", getAddress().getHostAddress());
        
        agentJsonObject.add("skills", new Gson().toJsonTree(getSkills()).getAsJsonArray());
        
        agentJsonObject.addProperty("activated", activated);
        
        agentJsonObject.addProperty("available", available);
        
        JsonObject assignedSupportRequestJsonObject = new JsonObject();
        
        if (hasAssignedSupportRequest()) {
            assignedSupportRequestJsonObject.addProperty("uuid", assignedSupportRequest.getUUID().toString());
            
            assignedSupportRequestJsonObject.add("user", assignedSupportRequest.getUser().toJson());
            
            assignedSupportRequestJsonObject.addProperty("address", assignedSupportRequest.getAddress().getHostAddress());
            
            assignedSupportRequestJsonObject.addProperty("type", assignedSupportRequest.getType().toString());
            
            assignedSupportRequestJsonObject.addProperty("priority", assignedSupportRequest.getPriority());
        }
        
        agentJsonObject.add("assigned_support_request", assignedSupportRequestJsonObject);
        
        return agentJsonObject;
    }
    
    public static Agent fromJson(JsonObject jsonObject)
    {
        return Agent.builder()
                .setAddress(getAsString(jsonObject, "address"))
                .setSkills(getAsBooleanMap(jsonObject, "skills"))
                .build();
    }
    
    public static AgentBuilder builder()
    {
        return new AgentBuilder();
    }
    
    public static class AgentBuilder
    {
        private String address;
        private Map<String, Boolean> skills;
        
        public AgentBuilder()
        {
            address = null;
            skills = new HashMap<>();
        }
        
        public AgentBuilder setAddress(String addressString)
        {
            address = addressString;
            
            return this;   
        }
        
        public AgentBuilder setAddress(InetAddress address)
        {
            this.address = address.getHostAddress().toString();
            
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
        
        public AgentBuilder setSkill(SupportRequest.Type requestType, boolean ableToService)
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
            return new Agent(this);
        }
    }
}
