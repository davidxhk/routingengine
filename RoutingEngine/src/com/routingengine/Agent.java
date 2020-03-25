package com.routingengine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.routingengine.json.JsonUtils;


public class Agent extends InetEntity
{
  private volatile HashMap<SupportRequest.Type, Boolean> skills;
  private volatile boolean active;
  private volatile boolean available;
  private volatile SupportRequest assignedSupportRequest;
  
  private Agent(AgentBuilder builder)
  {
    super(builder.address);
    skills = builder.skills;
    active = true;
    available = false;
    assignedSupportRequest = null;
  }
  
  public boolean ableToService(SupportRequest.Type requestType)
  {
    if (!active)
      return false;
    
    return skills.get(requestType);
  }
  
  public SupportRequest.Type[] getSkills()
  {
    return skills.entrySet().stream()
        .filter(entry -> entry.getValue())
        .map(entry -> entry.getKey())
        .toArray(SupportRequest.Type[]::new);
  }
  
  public void setSkill(int requestTypeIndex, Boolean ableToService)
  {
    setSkill(SupportRequest.Type.of(requestTypeIndex), ableToService);
  }
  
  public void setSkill(String requestTypeString, Boolean ableToService)
  {
    setSkill(SupportRequest.Type.of(requestTypeString), ableToService);
  }
  
  public void setSkill(SupportRequest.Type requestType, Boolean ableToService)
  {
    if (ableToService == null)
      throw new IllegalArgumentException("ability to service missing");
    
    skills.put(requestType, ableToService);
  }  
  
  public void setSkills(Map<String, Boolean> skills)
  {
    skills.entrySet().stream()
      .filter(entry -> SupportRequest.Type.is(entry.getKey()))
      .forEach(entry -> setSkill(entry.getKey(), entry.getValue()));
  }
  
  public boolean isActive()
  {
    return active;
  }
  
  public void setInactive()
  {
    available = false;
    active = false;
  }
  
  public boolean isAvailable()
  {
    return available;
  }
  
  public void setAvailability(Boolean isAvailable)
  {
    if (isAvailable == null)
      throw new IllegalArgumentException("available missing");
    
    if (!active)
      return;
    
    available = isAvailable;
  }
  
  public SupportRequest getAssignedSupportRequest()
  {
    return assignedSupportRequest;
  }
  
  public void setAssignedSupportRequest(SupportRequest supportRequest)
  {
    if (!active)
      return;
    
    assignedSupportRequest = supportRequest;
    
    available = !hasAssignedSupportRequest();
  }
  
  public boolean hasAssignedSupportRequest()
  { 
    return assignedSupportRequest != null;
  }
  
  public JsonObject toJson()
  {
    JsonObject agentJsonObject = new JsonObject();
    
    agentJsonObject.addProperty("uuid", getUUID().toString());
    
    agentJsonObject.addProperty("address", getAddress().getHostAddress());
    
    agentJsonObject.add("skills", new Gson().toJsonTree(getSkills()).getAsJsonArray());
    
    agentJsonObject.addProperty("active", active);
    
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
        .setAddress(JsonUtils.getAsString(jsonObject, "address"))
        .setSkills(JsonUtils.getAsBooleanMap(jsonObject, "skills"))
        .build();
  }
  
  public static AgentBuilder builder()
  {
    return new AgentBuilder();
  }
  
  public static class AgentBuilder
  {
    private InetAddress address;
    private HashMap<SupportRequest.Type, Boolean> skills;
    
    public AgentBuilder()
    {
      address = null;
      skills = new HashMap<>();
      
      for (SupportRequest.Type requestType : SupportRequest.Type.values()) {
        skills.put(requestType, false);
      }
    }
    
    public AgentBuilder setAddress(String address)
    {
      try {
        return setAddress(InetAddress.getByName(address));
      }
      
      catch (UnknownHostException exception) {
        throw new IllegalArgumentException("address invalid");
      }
    }
    
    public AgentBuilder setAddress(InetAddress address)
    {
      this.address = address;
      
      return this;
    }
    
    public AgentBuilder setSkill(int requestTypeIndex, boolean ableToService)
    {
      return setSkill(SupportRequest.Type.of(requestTypeIndex), ableToService);
    }
    
    public AgentBuilder setSkill(String requestTypeString, boolean ableToService)
    {
      return setSkill(SupportRequest.Type.of(requestTypeString), ableToService);
    }
    
    public AgentBuilder setSkill(SupportRequest.Type requestType, boolean ableToService)
    {
      skills.put(requestType, ableToService);
      
      return this;
    }
    
    public AgentBuilder setSkills(Map<String, Boolean> skills)
    {
      if (skills != null) {
        skills.entrySet().stream()
          .filter(entry -> SupportRequest.Type.is(entry.getKey()))
          .forEach(entry -> setSkill(entry.getKey(), entry.getValue()));
      }
      
      return this;
    }
    
    public Agent build()
    {
      if (address == null)
        throw new IllegalArgumentException("address missing");
      
      for (boolean ableToService : skills.values()) {
        if (ableToService)
          return new Agent(this);
      }
      
      throw new IllegalArgumentException("skill missing");
    }
  }
}