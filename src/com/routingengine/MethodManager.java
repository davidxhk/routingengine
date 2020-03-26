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
            Map.entry("ping",                        "PingMethod"),
            Map.entry("new_support_request",         "NewSupportRequestMethod"),
            Map.entry("check_support_request",       "CheckSupportRequestMethod"),
            Map.entry("change_support_request_type", "ChangeSupportRequestTypeMethod"),
            Map.entry("wait_for_agent",              "WaitForAgentMethod"),
            Map.entry("close_support_request",       "CloseSupportRequestMethod"),
            Map.entry("remove_support_request",      "RemoveSupportRequestMethod"),
            Map.entry("new_agent",                   "NewAgentMethod"),
            Map.entry("check_agent",                 "CheckAgentMethod"),
            Map.entry("update_agent_skills",         "UpdateAgentSkillsMethod"),
            Map.entry("update_agent_availability",   "UpdateAgentAvailabilityMethod"),
            Map.entry("take_support_request",        "TakeSupportRequestMethod"),
            Map.entry("drop_support_request",        "DropSupportRequestMethod"),
            Map.entry("remove_agent",                "RemoveAgentMethod"),
            Map.entry("get_status_overview",         "GetStatusOverviewMethod"),
            Map.entry("get_agent_status",            "GetAgentStatusMethod"),
            Map.entry("get_support_request_status",  "GetSupportRequestStatusMethod"),
            Map.entry("get_queue_status",            "GetQueueStatusMethod")
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
            Method methodInstance = (Method) Class.forName("com.routingengine.methods." + methodClassName)
                .getConstructor()
                .newInstance();
            
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
