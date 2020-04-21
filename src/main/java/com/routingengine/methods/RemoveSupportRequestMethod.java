package com.routingengine.methods;

import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class RemoveSupportRequestMethod extends AbstractSupportRequestAdminMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest supportRequest = getSupportRequest(request);
        
        routingEngine.removeSupportRequest(supportRequest.getUUID());
        
        if (supportRequest.isOpen())
            supportRequest.close();
        
        return JsonResponse.success(request, supportRequest.toJson());
    }
}
