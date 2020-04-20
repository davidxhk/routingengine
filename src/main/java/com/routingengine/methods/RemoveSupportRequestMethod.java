package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class RemoveSupportRequestMethod extends AbstractSupportRequestAdminMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = getSupportRequest(arguments);
        
        routingEngine.removeSupportRequest(supportRequest.getUUID());
        
        if (supportRequest.isOpen())
            supportRequest.close();
        
        return supportRequest.toJson();
    }
}
