package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodManager;


public class NewAgentMethod extends MethodManager.Method
{
  @Override
  public JsonElement handle(JsonObject arguments)
  {
    Agent agent = newAgent(arguments);
    
    addAgent(agent);
    
    return agent.toJson();
  }
  
  public Agent newAgent(JsonObject arguments)
  {
    return Agent.fromJson(arguments);
  }
  
  public void addAgent(Agent agent)
  {
    routingEngine.addAgent(agent);
  }
}
