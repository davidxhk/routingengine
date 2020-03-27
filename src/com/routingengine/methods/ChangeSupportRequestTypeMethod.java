package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


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
        String supportRequestTypeString = getAsString(arguments, "type");
        
        supportRequest.setType(supportRequestTypeString);
    }
}
