package com.routingengine.methods;

import static com.routingengine.MethodManager.Method;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class NewSupportRequestMethod extends Method
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = newSupportRequest(arguments);
        
        addSupportRequest(supportRequest);
        
        return supportRequest.toJson();
    }
    
    public SupportRequest newSupportRequest(JsonObject arguments)
    {
        return SupportRequest.fromJson(arguments);
    }
    
    public void addSupportRequest(SupportRequest supportRequest)
    {
        routingEngine.addSupportRequest(supportRequest);
    }
}
