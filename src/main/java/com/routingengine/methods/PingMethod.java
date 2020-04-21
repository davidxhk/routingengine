package com.routingengine.methods;

import com.google.gson.JsonPrimitive;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class PingMethod extends AbstractAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        JsonPrimitive payload = new JsonPrimitive("pong");
        
        return JsonResponse.success(request, payload);
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
