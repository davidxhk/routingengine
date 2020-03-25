package com.routingengine;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public final class MethodManager
{
  private static final Map<String, String> methodClassNameMap;
  
  static {
    methodClassNameMap = Map.ofEntries(  
      Map.entry("ping",                        "com.routingengine.methods.PingMethod"),
      Map.entry("new_support_request",         "com.routingengine.methods.NewSupportRequestMethod"),
      Map.entry("check_support_request",       "com.routingengine.methods.CheckSupportRequestMethod"),
      Map.entry("change_support_request_type", "com.routingengine.methods.ChangeSupportRequestTypeMethod"),
      Map.entry("wait_for_agent",              "com.routingengine.methods.WaitForAgentMethod"),
      Map.entry("close_support_request",       "com.routingengine.methods.CloseSupportRequestMethod"),
      Map.entry("remove_support_request",      "com.routingengine.methods.RemoveSupportRequestMethod"),
      Map.entry("new_agent",                   "com.routingengine.methods.NewAgentMethod"),
      Map.entry("check_agent",                 "com.routingengine.methods.CheckAgentMethod"),
      Map.entry("update_agent_skills",         "com.routingengine.methods.UpdateAgentSkillsMethod"),
      Map.entry("update_agent_availability",   "com.routingengine.methods.UpdateAgentAvailabilityMethod"),
      Map.entry("take_support_request",        "com.routingengine.methods.TakeSupportRequestMethod"),
      Map.entry("drop_support_request",        "com.routingengine.methods.DropSupportRequestMethod"),
      Map.entry("remove_agent",                "com.routingengine.methods.RemoveAgentMethod"),
      Map.entry("get_status_overview",         "com.routingengine.methods.GetStatusOverviewMethod"),
      Map.entry("get_agent_status",            "com.routingengine.methods.GetAgentStatusMethod"),
      Map.entry("get_support_request_status",  "com.routingengine.methods.GetSupportRequestStatusMethod"),
      Map.entry("get_queue_status",            "com.routingengine.methods.GetQueueStatusMethod")
    );
  }
  
  public static final boolean supports(String method)
  {
    return MethodManager.methodClassNameMap.containsKey(method);
  }
  
  public static abstract class Method
  {
    protected RoutingEngine routingEngine;
    
    public abstract JsonElement handle(JsonObject arguments);
  }
  
  private RoutingEngine routingEngine;
  private final Map<String, Method> methodMap;
  
  public MethodManager(RoutingEngine routingEngine)
  {
    this.routingEngine = routingEngine;
    
    methodMap = new HashMap<>();
  }
  
  private final void instantiateMethod(String method)
  {
    if (!MethodManager.supports(method))
      throw new IllegalArgumentException("invalid method");
    
    String methodClassName = MethodManager.methodClassNameMap.get(method);
    
    try {
      Method methodInstance = (Method) Class.forName(methodClassName).getConstructor().newInstance();
      
      methodInstance.routingEngine = this.routingEngine;
      
      methodMap.put(method, methodInstance);
    }
    
    catch (Exception exception) {
      throw new IllegalArgumentException("failed to instantiate " + method);
    }
  }
  
  public final Method getMethod(String method)
  {
    if (!MethodManager.supports(method))
      throw new IllegalArgumentException("invalid method");
    
    if (!methodMap.containsKey(method))
      instantiateMethod(method);
    
    Method methodInstance = methodMap.get(method);
    
    return methodInstance;
  }
  
  public final JsonElement handle(String method, JsonObject arguments)
  {
    Method methodInstance = getMethod(method);
    
    JsonElement payload = methodInstance.handle(arguments);
    
    return payload;
  }
}
