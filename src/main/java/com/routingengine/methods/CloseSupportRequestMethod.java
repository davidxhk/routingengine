package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class CloseSupportRequestMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        supportRequest.close();
        
        return supportRequest.toJson();
    }
}
