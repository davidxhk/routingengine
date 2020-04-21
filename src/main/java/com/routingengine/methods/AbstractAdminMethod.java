package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.json.JsonRequest;


public abstract class AbstractAdminMethod extends AbstractMethod
{
    @Override
    protected void beforeHandle(JsonRequest request)
    {
        super.beforeHandle(request);
        
        if (requiresAdminRights()) {
            String adminUUIDString = getAsString(request, "admin_uuid");
            
            routingEngine.verifyAdmin(adminUUIDString);
        }
    }
    
    protected abstract boolean requiresAdminRights();
}
