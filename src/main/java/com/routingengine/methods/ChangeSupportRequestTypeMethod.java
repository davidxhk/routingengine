package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class ChangeSupportRequestTypeMethod extends AbstractSupportRequestMethod
{    
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        String supportRequestTypeString = getAsString(arguments, "type");
        
        supportRequest.setType(supportRequestTypeString);
        
        return supportRequest.toJson();
    }
}
