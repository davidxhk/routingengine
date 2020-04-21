package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ChangeSupportRequestTypeMethod extends AbstractSupportRequestMethod
{    
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest supportRequest = getSupportRequest(request);
        
        String supportRequestTypeString = getAsString(request, "type");
        
        supportRequest.setType(supportRequestTypeString);
        
        return JsonResponse.success(request, supportRequest.toJson());
    }
}
