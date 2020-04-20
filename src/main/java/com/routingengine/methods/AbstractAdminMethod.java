package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonObject;


public abstract class AbstractAdminMethod extends AbstractMethod
{
    @Override
    protected JsonObject beforeHandle(JsonObject arguments)
    {
        if (requiresAdminRights()) {
            String adminUUIDString = getAsString(arguments, "admin_uuid");
            
            routingEngine.verifyAdmin(adminUUIDString);
        }
        
        return arguments;
    }
    
    protected abstract boolean requiresAdminRights();
}
