package com.routingengine.methods;

import com.routingengine.RoutingEngine;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public interface Method
{
    public void setRoutingEngine(RoutingEngine routingEngine);
    
    public abstract JsonResponse handleRequest(JsonRequest request);
}
