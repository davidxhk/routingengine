package com.routingengine.methods;

import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class CloseSupportRequestMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest supportRequest = getSupportRequest(request);
        
        supportRequest.close();
        
        return JsonResponse.success(request, supportRequest.toJson());
    }
}
