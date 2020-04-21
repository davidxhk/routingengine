package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.RoutingEngine.newUUID;
import com.google.gson.JsonObject;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewAdminMethod extends AbstractAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        String newAdminUUIDString;
        
        newAdminUUIDString = getAsString(request, "new_admin_uuid");
        
        if (newAdminUUIDString == null)
            newAdminUUIDString = newUUID();
        
        routingEngine.addAdmin(newAdminUUIDString);
        
        JsonObject payload = new JsonObject();
        
        payload.addProperty("new_admin_uuid", newAdminUUIDString);
        
        return JsonResponse.success(request, payload);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return true;
    }
}
