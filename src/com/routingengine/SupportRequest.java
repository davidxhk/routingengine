package com.routingengine;

import java.net.InetAddress;
import java.util.concurrent.TimeoutException;
import com.google.gson.JsonObject;
import com.routingengine.json.JsonUtils;


public class SupportRequest extends InetEntity
    implements Comparable<SupportRequest>
{
    private final User user;
    private volatile Type requestType;
    private volatile boolean open;
    private volatile boolean waiting;
    private volatile boolean notified;
    private volatile Agent assignedAgent;
    private volatile int priority;
    private static final long TIMEOUT_MILLIS = 30000L;
    
    private SupportRequest(SupportRequestBuilder builder)
    {
        super(builder.address);
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
        setType(SupportRequest.Type.of(requestTypeString));
    }
    
    public synchronized void setType(Integer requestTypeIndex)
    {
        setType(SupportRequest.Type.of(requestTypeIndex));
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
        
        JsonObject assignedAgentJsonObject = new JsonObject();
        
        if (hasAssignedAgent()) {
            assignedAgentJsonObject.addProperty("address", assignedAgent.getAddress().getHostAddress());
            assignedAgentJsonObject.addProperty("available", assignedAgent.isAvailable());
            
        }
        
        supportRequestJsonObject.add("assigned_agent", assignedAgentJsonObject);
        
        supportRequestJsonObject.addProperty("priority", priority);
        
        return supportRequestJsonObject;
    }
    
    public static SupportRequest fromJson(JsonObject jsonObject)
    {
        return builder()
                .setName(JsonUtils.getAsString(jsonObject, "name"))
                .setEmail(JsonUtils.getAsString(jsonObject, "email"))
                .setType(JsonUtils.getAsString(jsonObject, "type"))
                .setAddress(JsonUtils.getAsString(jsonObject, "address"))
                .build();
    }
    
    public static SupportRequestBuilder builder()
    {
        return new SupportRequestBuilder();
    }
    
    public static class SupportRequestBuilder
    {
        private String name;
        private String email;
        private String requestType;
        private String address;
        
        public SupportRequestBuilder()
        {
            name = null;
            email = null;
            requestType = null;
            address = null;
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
        
        public SupportRequestBuilder setAddress(String addressString)
        {
            address = addressString;
            
            return this;   
        }
        
        public SupportRequestBuilder setAddress(InetAddress address)
        {
            this.address = address.getHostAddress().toString();
            
            return this;
        }
        
        public SupportRequest build()
        {   
            return new SupportRequest(this);
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
