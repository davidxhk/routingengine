package com.routingengine.methods;

import static com.routingengine.json.JsonUtils.getAsString;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class NewSupportRequestMethod extends AbstractSupportRequestMethod
{
    @Override
    public JsonElement handle(JsonObject arguments)
    {
        SupportRequest supportRequest = newSupportRequest(arguments);
        
        routingEngine.addSupportRequest(supportRequest);
        
        return supportRequest.toJson();
    }
    
    public SupportRequest newSupportRequest(JsonObject arguments)
    {
        return SupportRequest.builder()
            .setName(getAsString(arguments, "name"))
            .setEmail(getAsString(arguments, "email"))
            .setType(getAsString(arguments, "type"))
            .setAddress(getAsString(arguments, "address"))
            .build();
    }
}
