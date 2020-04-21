package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewSupportRequestMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonResponse handle(JsonRequest request)
    {
        SupportRequest supportRequest = newSupportRequest(request);
        
        routingEngine.addSupportRequest(supportRequest);
        
        return JsonResponse.success(request, supportRequest.toJson());
    }
    
    public SupportRequest newSupportRequest(JsonRequest request)
    {
        return SupportRequest.builder()
            .setName(getAsString(request, "name"))
            .setEmail(getAsString(request, "email"))
            .setType(getAsString(request, "type"))
            .setAddress(getAsString(request, "address"))
            .build();
    }
}
