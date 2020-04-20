package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public abstract class AbstractSupportRequestMethod extends AbstractAdminMethod
{
    protected final SupportRequest getSupportRequest(JsonObject arguments)
    {
        String supportRequestUUIDString = getAsString(arguments, "uuid");
        
        SupportRequest supportRequest = routingEngine.getSupportRequest(supportRequestUUIDString);
        
        updateAddress(supportRequest, arguments);
        
        return supportRequest;
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
