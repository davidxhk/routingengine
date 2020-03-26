package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.MethodManager;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonUtils;


public class CheckSupportRequestMethod extends MethodManager.Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        return supportRequest.toJson();
    }
    
    public SupportRequest getSupportRequest(JsonObject arguments)
    {
        String supportRequestUUIDString = JsonUtils.getAsString(arguments, "uuid");
        
        return routingEngine.getSupportRequest(supportRequestUUIDString);
    }
}
