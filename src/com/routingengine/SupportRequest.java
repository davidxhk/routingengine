package com.routingengine;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.getAsBoolean;
import static com.routingengine.json.JsonUtils.getAsInt;
import static com.routingengine.json.JsonUtils.getAsJsonObject;
import java.util.concurrent.TimeoutException;
import com.google.gson.JsonObject;


public class SupportRequest extends InetEntity
    implements Comparable<SupportRequest>
{
    private User user;
    private volatile Type requestType;
    private volatile boolean open;
    private volatile boolean waiting;
    private volatile boolean notified;
    private volatile Agent assignedAgent;
    private volatile int priority;
    private static final long TIMEOUT_MILLIS = 30000L;
    
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
        setType(builder.requestType);
        
        open = true;
        waiting = false;
        notified = false;
        assignedAgent = null;
        priority = 1;
    }
    
    public User getUser()
    {
        return user;
    }

    public synchronized Type getType()
    {
        return requestType;
    }
    
    public synchronized void setType(String requestTypeString)
    {
        setType(Type.of(requestTypeString));
    }
    
    public synchronized void setType(Integer requestTypeIndex)
    {
        setType(Type.of(requestTypeIndex));
    }

    public synchronized void setType(Type requestType)
    {
        if (requestType == null)
            throw new IllegalArgumentException("type missing");
        
        this.requestType = requestType;
    }

    public synchronized boolean isOpen()
    {
        return open;
    }

    public synchronized void close()
    {
        open = false;
    }
    
    public synchronized boolean isWaiting()
    {
        return waiting;
    }

    public synchronized void startWaiting()
        throws InterruptedException, TimeoutException
    {
        startWaiting(TIMEOUT_MILLIS);
    }
    
    public synchronized void startWaiting(long timeout_millis)
        throws InterruptedException, TimeoutException
    {
        waiting = true;
        
        wait(timeout_millis);
        
        waiting = false;
        
        if (notified)
            notified = false;
        
        else
            throw new TimeoutException();
    }
    
    public synchronized void stopWaiting()
    {
        notified = true;
        
        notify();
    }
    
    public synchronized Agent getAssignedAgent()
    {
        return assignedAgent;
    }

    public synchronized void setAssignedAgent(Agent agent)
    {    
        assignedAgent = agent;
    }

    public synchronized boolean hasAssignedAgent()
    {
        return assignedAgent != null;
    }
    
    public synchronized int getPriority()
    {
        return priority;
    }

    public synchronized void incrementPriority()
    {
        priority++;
    }
    
    public synchronized void doublePriority()
    {
        priority *= 2;
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
        
        supportRequestJsonObject.addProperty("type", requestType.toString());
        
        supportRequestJsonObject.addProperty("open", open);
        
        supportRequestJsonObject.addProperty("waiting", waiting);
        
        JsonObject assignedAgentJsonObject = null;
        
        if (hasAssignedAgent()) {
            assignedAgentJsonObject = new JsonObject();
            
            assignedAgentJsonObject.addProperty("address", assignedAgent.getAddress().getHostAddress());
            
            assignedAgentJsonObject.addProperty("available", assignedAgent.isAvailable());
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
                JsonObject skills = new JsonObject();
                skills.addProperty(supportRequest.getType().toString(), true);
                agentJson.add("skills", skills);
                
                supportRequest.setAssignedAgent(Agent.fromJson(agentJson));
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
        private String requestType;
        
        public SupportRequestBuilder()
        {
            uuid = null;
            address = null;
            name = null;
            email = null;
            requestType = null;
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
        
        public SupportRequestBuilder setType(int requestTypeIndex)
        {
            requestType = String.valueOf(requestTypeIndex);
            
            return this;
        }
        
        public SupportRequestBuilder setType(String requestTypeString)
        {
            requestType = requestTypeString;
            
            return this;
        }
        
        public SupportRequestBuilder setType(Type requestType)
        {
            this.requestType = requestType.toString();
            
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
    
    public enum Type
    {
        GENERAL_ENQUIRY,
        CHECK_BILL,
        CHECK_SUBSCRIPTION;
        
        public static boolean is(String requestTypeString)
        {
            try {
                of(requestTypeString);
                
                return true;
            }
            
            catch (IllegalArgumentException exception) {
                return false;
            }
        }
        
        public static Type of(Integer requestTypeIndex)
        {
            if (requestTypeIndex == null)
                throw new IllegalArgumentException("type missing");
            
            Type[] types = values();
            
            if (requestTypeIndex < 0 || requestTypeIndex > types.length-1)
                throw new IllegalArgumentException("type index out of bounds");
            
            return types[requestTypeIndex];
        }
        
        public static Type of(String requestTypeString)
        {
            if (requestTypeString == null)
                throw new IllegalArgumentException("type missing");
            
            try {
                return of(Integer.valueOf(requestTypeString));
            }
            
            catch (NumberFormatException exception) {
                try {
                    return Type.valueOf(requestTypeString);    
                }
                
                catch (IllegalArgumentException exception2) {
                    throw new IllegalArgumentException("type string invalid");
                }
            }
        }
        
        public static Type of(Type requestType)
        {
            if (requestType == null)
                throw new IllegalArgumentException("type missing");
            
            return requestType;
        }
    }
}
