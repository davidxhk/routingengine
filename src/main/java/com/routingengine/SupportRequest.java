package com.routingengine;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.getAsBoolean;
import static com.routingengine.json.JsonUtils.getAsInt;
import static com.routingengine.json.JsonUtils.getAsJsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class SupportRequest extends InetEntity
    implements Comparable<SupportRequest>
{
    User user;
    volatile Type type;
    volatile boolean open;
    volatile boolean waiting;
    volatile Agent assignedAgent;
    volatile int priority;
    
    private SupportRequest()
    {
        super();
    }
    
    private SupportRequest(String address)
    {
        super();
        
        super.setAddress(address);
    }
    
    private SupportRequest(String uuid, String address)
    {
        super(uuid, address);
    }
    
    private void initialize(SupportRequestBuilder builder)
    {
        user = new User(builder.name, builder.email);
        setType(builder.type);
        
        open = true;
        waiting = false;
        assignedAgent = null;
        priority = 1;
    }
    
    public User getUser()
    {
        return user;
    }

    public synchronized Type getType()
    {
        return type;
    }
    
    public synchronized void setType(String typeString)
    {
        setType(Type.of(typeString));
    }
    
    public synchronized void setType(Integer typeIndex)
    {
        setType(Type.of(typeIndex));
    }

    public synchronized void setType(Type type)
    {
        if (type == null)
            throw new IllegalArgumentException("type missing");
        
        this.type = type;
    }
    
    public synchronized boolean isOpen()
    {
        return open;
    }
    
    public synchronized void close()
    {
        if (!open)
            throw new IllegalStateException("support request already closed");
        
        if (hasAssignedAgent())
            dropAssignedAgent();
        
        waiting = false;
        open = false;
    }
    
    public synchronized boolean isWaiting()
    {
        return waiting;
    }
    
    public synchronized void startWaiting()
    {
        if (!open)
            throw new IllegalStateException("support request is closed");
        
        if (waiting)
            throw new IllegalStateException("support request already waiting");
        
        waiting = true;
    }
    
    public synchronized void stopWaiting()
    {
        if (!open)
            throw new IllegalStateException("support request is closed");
        
        if (!waiting)
            throw new IllegalStateException("support request not waiting");
        
        waiting = false;
    }
    
    public synchronized Agent getAssignedAgent()
    {
        return assignedAgent;
    }
    
    public synchronized boolean hasAssignedAgent()
    {
        return assignedAgent != null;
    }
    
    public synchronized void setAssignedAgent(Agent agent)
    {
        if (agent == null)
            throw new IllegalArgumentException("agent missing");
        
        if (!open)
            throw new IllegalStateException("support request is closed");
        
        if (assignedAgent != null)
            throw new IllegalStateException("support request already has assigned agent");
        
        agent.setAssignedSupportRequest(this);
    }
    
    public synchronized void dropAssignedAgent()
    {
        if (assignedAgent == null)
            throw new IllegalStateException("support request has no assigned agent");
        
        if (assignedAgent.assignedSupportRequest == this)
            assignedAgent.dropAssignedSupportRequest();
        
        else
            assignedAgent = null;
    }
    
    public synchronized int getPriority()
    {
        return priority;
    }

    public synchronized void incrementPriority()
    {
        priority++;
    }
    
    @Override
    public int compareTo(SupportRequest other)
    {
        if (this.getType() != other.getType())
            return 0;
        
        else if (!other.isOpen())
            return -1;
        
        else if (!this.isOpen())
            return 1;
        
        else if (this.getPriority() > other.getPriority())
            return -1;
        
        else if (this.getPriority() < other.getPriority())
            return 1;
        
        else 
            return 0;
    }
    
    public synchronized JsonObject toJson()
    {
        JsonObject supportRequestJsonObject = new JsonObject();
        
        supportRequestJsonObject.addProperty("uuid", getUUID().toString());
        
        supportRequestJsonObject.addProperty("address", getAddress().getHostAddress());
        
        supportRequestJsonObject.add("user", user.toJson());
        
        supportRequestJsonObject.addProperty("type", type.toString());
        
        supportRequestJsonObject.addProperty("open", open);
        
        supportRequestJsonObject.addProperty("waiting", waiting);
        
        JsonObject assignedAgentJsonObject = null;
        
        if (hasAssignedAgent()) {
            assignedAgentJsonObject = new JsonObject();
            
            assignedAgentJsonObject.addProperty("address", assignedAgent.getAddress().getHostAddress());
            
            assignedAgentJsonObject.addProperty("available", assignedAgent.available);
        }
        
        supportRequestJsonObject.add("assigned_agent", assignedAgentJsonObject);
        
        supportRequestJsonObject.addProperty("priority", priority);
        
        return supportRequestJsonObject;
    }
    
    public static SupportRequest fromJson(JsonObject jsonObject)
    {
        SupportRequest supportRequest = builder()
            .setUUID(getAsString(jsonObject, "uuid"))
            .setAddress(getAsString(jsonObject, "address"))
            .setName(getAsString(jsonObject, "name"))
            .setEmail(getAsString(jsonObject, "email"))
            .setUser(User.fromJson(getAsJsonObject(jsonObject, "user")))
            .setType(getAsString(jsonObject, "type"))
            .build();
        
        if (jsonObject.has("open"))
            supportRequest.open = getAsBoolean(jsonObject, "open");
        
        if (jsonObject.has("waiting"))
            supportRequest.waiting = getAsBoolean(jsonObject, "waiting");
        
        if (jsonObject.has("assigned_agent")) {
            JsonObject agentJson = getAsJsonObject(jsonObject, "assigned_agent");
            
            if (agentJson != null) {
                JsonArray skills = new JsonArray();
                skills.add(supportRequest.getType().toString());
                agentJson.add("skills", skills);
                
                supportRequest.assignedAgent = Agent.fromJson(agentJson);
            }
        }
        
        if (jsonObject.has("priority"))
            supportRequest.priority = getAsInt(jsonObject, "priority");
        
        return supportRequest;
    }
    
    public static SupportRequestBuilder builder()
    {
        return new SupportRequestBuilder();
    }
    
    public static class SupportRequestBuilder
    {
        private String uuid;
        private String address;
        private String name;
        private String email;
        private String type;
        
        public SupportRequestBuilder()
        {
            uuid = null;
            address = null;
            name = null;
            email = null;
            type = null;
        }
        
        public SupportRequestBuilder setUUID(String UUIDString)
        {
            uuid = UUIDString;
            
            return this;   
        }
        
        public SupportRequestBuilder setAddress(String addressString)
        {
            address = addressString;
            
            return this;   
        }
        
        public SupportRequestBuilder setUser(User user)
        {
            if (user != null) {
                this.name = user.getName();
                this.email = user.getEmail();
            }
            
            return this;
        }
        
        public SupportRequestBuilder setName(String name)
        {
            this.name = name;
            
            return this;
        }
        
        public SupportRequestBuilder setEmail(String email)
        {
            this.email = email;
            
            return this;
        }
        
        public SupportRequestBuilder setType(int typeIndex)
        {
            type = String.valueOf(typeIndex);
            
            return this;
        }
        
        public SupportRequestBuilder setType(String typeString)
        {
            type = typeString;
            
            return this;
        }
        
        public SupportRequestBuilder setType(Type type)
        {
            this.type = type.toString();
            
            return this;
        }
        
        public SupportRequest build()
        {   
            SupportRequest supportRequest;
            
            if (uuid != null)
                supportRequest = new SupportRequest(uuid, address);
            
            else if (address != null)
                supportRequest = new SupportRequest(address);
            
            else
                supportRequest = new SupportRequest();
            
            supportRequest.initialize(this);
            
            return supportRequest;
        }
    }
    
    public static enum Type
    {
        GENERAL_ENQUIRY,
        CHECK_BILL,
        CHECK_SUBSCRIPTION;
        
        public static boolean is(String typeString)
        {
            try {
                of(typeString);
                
                return true;
            }
            
            catch (IllegalArgumentException exception) {
                return false;
            }
        }
        
        public static Type of(Integer typeIndex)
        {
            if (typeIndex == null)
                throw new IllegalArgumentException("type missing");
            
            Type[] types = values();
            
            if (typeIndex < 0 || typeIndex > types.length-1)
                throw new IllegalArgumentException("type index out of bounds");
            
            return types[typeIndex];
        }
        
        public static Type of(String typeString)
        {
            if (typeString == null)
                throw new IllegalArgumentException("type missing");
            
            try {
                return of(Integer.valueOf(typeString));
            }
            
            catch (NumberFormatException exception) {
                try {
                    return Type.valueOf(typeString);    
                }
                
                catch (IllegalArgumentException exception2) {
                    throw new IllegalArgumentException("type string invalid");
                }
            }
        }
        
        public static Type of(Type type)
        {
            if (type == null)
                throw new IllegalArgumentException("type missing");
            
            return type;
        }
    }
}
