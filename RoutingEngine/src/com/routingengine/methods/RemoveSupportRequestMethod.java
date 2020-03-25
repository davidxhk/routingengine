package com.routingengine.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;


public class RemoveSupportRequestMethod extends CloseSupportRequestMethod
{
  @Override
  public JsonElement handle(JsonObject arguments)
  {
    SupportRequest supportRequest = getSupportRequest(arguments);
    
    removeSupportRequest(supportRequest);
    
    return supportRequest.toJson();
  }
  
  public void removeSupportRequest(SupportRequest supportRequest)
  {
    closeSupportRequest(supportRequest);
    
    routingEngine.removeSupportRequest(supportRequest);
  }
}
