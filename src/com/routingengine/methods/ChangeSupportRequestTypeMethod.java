package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonUtils;


public class ChangeSupportRequestTypeMethod extends CheckSupportRequestMethod
{    
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        changeSupportRequestType(supportRequest, arguments);
        
        return supportRequest.toJson();
    }
    
    public static void changeSupportRequestType(SupportRequest supportRequest, JsonObject arguments)
    {
        String supportRequestTypeString = JsonUtils.getAsString(arguments, "type");
        
        supportRequest.setType(supportRequestTypeString);
    }
}
