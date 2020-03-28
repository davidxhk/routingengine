package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import static com.routingengine.MethodManager.Method;
import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.SupportRequest;


public class RemoveSupportRequestMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = removeSupportRequest(arguments);
        
        return supportRequest.toJson();
    }
    
    public SupportRequest removeSupportRequest(JsonObject arguments)
    {
        String supportRequestUUIDString = getAsString(arguments, "uuid");
        
        SupportRequest supportRequest = routingEngine.removeSupportRequest(supportRequestUUIDString);
        
        if (supportRequest.isOpen())
            supportRequest.close();
        
        return supportRequest;
    }
}
