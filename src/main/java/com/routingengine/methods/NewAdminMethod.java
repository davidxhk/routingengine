package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.RoutingEngine.newUUID;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class NewAdminMethod extends AbstractAdminMethod
{
    @Override
    protected JsonElement handle(JsonObject arguments)
    {
        String newAdminUUIDString;
        
        try {
            newAdminUUIDString = getAsString(arguments, "new_admin_uuid");
        }
        
        catch (IllegalArgumentException exception) {
            ensureMissingException(exception);
            
            newAdminUUIDString = newUUID();
        }
        
        routingEngine.addAdmin(newAdminUUIDString);
        
        JsonObject payload = new JsonObject();
        
        payload.addProperty("new_admin_uuid", newAdminUUIDString);
        
        return payload;
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return true;
    }
}
