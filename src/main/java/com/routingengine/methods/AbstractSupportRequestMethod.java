package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;


public abstract class AbstractSupportRequestMethod extends AbstractAdminMethod
{
    protected final SupportRequest getSupportRequest(JsonRequest request)
    {
        String supportRequestUUIDString = getAsString(request, "uuid");
        
        SupportRequest supportRequest = routingEngine.getSupportRequest(supportRequestUUIDString);
        
        updateAddress(supportRequest, request);
        
        return supportRequest;
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
