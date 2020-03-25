package com.routingengine;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    requestType = builder.requestType;
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

  public Type getType()
  {
    return requestType;
  }
  
  public void setType(String requestTypeString)
  {
    setType(SupportRequest.Type.of(requestTypeString));
  }
  
  public void setType(Integer requestTypeIndex)
  {
    setType(SupportRequest.Type.of(requestTypeIndex));
  }

  public void setType(Type requestType)
  {
    if (requestType == null)
      throw new IllegalArgumentException("type missing");
    
    this.requestType = requestType;
  }

  public boolean isOpen()
  {
    return open;
  }

  public void close()
  {
    open = false;
  }
  
  public boolean isWaiting()
  {
    return waiting;
  }

  public void startWaiting()
    throws InterruptedException, TimeoutException
  {
    startWaiting(TIMEOUT_MILLIS);
  }
  
  public void startWaiting(long timeout_millis)
    throws InterruptedException, TimeoutException
  {
    synchronized(this) {
      waiting = true;
      
      wait(timeout_millis);
      
      waiting = false;
      
      if (notified)
        notified = false;
      
      else
        throw new TimeoutException();
    }
  }
  
  public void stopWaiting()
  {
    synchronized(this) {
      notified = true;
      
      notify();
    }
  }
  
  public Agent getAssignedAgent()
  {
    return assignedAgent;
  }

  public void setAssignedAgent(Agent agent)
  {  
    assignedAgent = agent;
  }

  public boolean hasAssignedAgent()
  {
    return assignedAgent != null;
  }
  
  public int getPriority()
  {
    return priority;
  }

  public void incrementPriority()
  {
    priority++;
  }
  
  public void doublePriority()
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
  
  public JsonObject toJson()
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
    private Type requestType;
    private InetAddress address;
    
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
      return setType(Type.of(requestTypeIndex));
    }
    
    public SupportRequestBuilder setType(String requestTypeString)
    {
      return setType(Type.of(requestTypeString));
    }
    
    public SupportRequestBuilder setType(Type requestType)
    {
      this.requestType = requestType;
      
      return this;
    }
    
    public SupportRequestBuilder setAddress(String address)
    {
      try {
        return setAddress(InetAddress.getByName(address));  
      }
      
      catch (UnknownHostException exception) {
        throw new IllegalArgumentException("address invalid");
      }
    }
    
    public SupportRequestBuilder setAddress(InetAddress address)
    {
      this.address = address;
      
      return this;
    }
    
    public SupportRequest build()
    {
      if (name == null)
        throw new IllegalArgumentException("name missing");
      
      if (email == null)
        throw new IllegalArgumentException("email missing");
      
      if (requestType == null)
        throw new IllegalArgumentException("type missing");
      
      if (address == null)
        throw new IllegalArgumentException("address missing");
      
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
        return of(requestTypeString) != null;
      }
      
      catch (IllegalArgumentException exception) {
        return false;
      }
    }
    
    public static Type of(Integer requestTypeIndex)
    {
      if (requestTypeIndex == null)
        throw new IllegalArgumentException("type index missing");
      
      Type[] types = values();
      
      if (requestTypeIndex < 0 || requestTypeIndex > types.length-1)
        throw new IllegalArgumentException("type index out of bounds");
      
      return types[requestTypeIndex];
    }
    
    public static Type of(String requestTypeString)
    {
      if (requestTypeString == null)
        throw new IllegalArgumentException("type string missing");
      
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
