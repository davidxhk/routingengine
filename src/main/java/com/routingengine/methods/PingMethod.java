package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.toJsonElement;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class PingMethod extends AbstractAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        return JsonResponse.success(request, toJsonElement("pong"));
    }
    
    @Override
    protected boolean requiresAdminRights()
    {
        return false;
    }
}
