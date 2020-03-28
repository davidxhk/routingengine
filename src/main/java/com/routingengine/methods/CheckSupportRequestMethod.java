package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class CheckSupportRequestMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        return supportRequest.toJson();
    }
    
    public SupportRequest getSupportRequest(JsonObject arguments)
    {
        String supportRequestUUIDString = getAsString(arguments, "uuid");
        
        return routingEngine.getSupportRequest(supportRequestUUIDString);
    }
}
