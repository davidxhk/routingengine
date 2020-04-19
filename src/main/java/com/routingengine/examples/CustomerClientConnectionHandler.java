package com.routingengine.examples;

import static com.routingengine.json.JsonUtils.getAsString;
import static com.routingengine.json.JsonUtils.castToString;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.routingengine.Logger;
import com.google.gson.JsonElement;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class CustomerClientConnectionHandler extends ClientConnectionHandler
{
    public Random random;
    public int id;
    private static final int MIN_TIMEOUT_MILLIS = 2000;
    private static final int MAX_TIMEOUT_MILLIS = 8000;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException
    {
        log("initialized!");
        
        randomSleep();
        
        log("creating new support request");
        newSupportRequest(
            "Customer " + id,
            "Customer" + id + "@gmail.com",
            (id-1) % 3);
        JsonResponse response = awaitResponse();
        
        String supportRequestUUIDString = getUUID(response);
        log("uuid -> " + supportRequestUUIDString);
        
        randomSleep();
        
        while (true) {
            log("waiting for agent");
            waitForAgent(supportRequestUUIDString);
            response = awaitResponse();
            
            if (response.didSucceed())
                break;
            
            else {
                String error = castToString(response.getPayload());
                
                if (!"wait for agent timeout".matches(error)) {
                    log("had unexpected error -> "+ error);
                    
                    log("exiting");
                    exit();
                }
            }
        }
        
        randomSleep();
        
        log("closing support request");
        closeSupportRequest(supportRequestUUIDString);
        awaitResponse();
        
        log("exiting");
        exit();
    }
    
    protected final void randomSleep()
        throws InterruptedException
    {
        int timeout = random.nextInt(MAX_TIMEOUT_MILLIS - MIN_TIMEOUT_MILLIS) + MIN_TIMEOUT_MILLIS;
        TimeUnit.MILLISECONDS.sleep(timeout);
    }
    
    protected final void log(JsonResponse jsonResponse)
    {
        log(jsonResponse.toString());
    }
    
    protected void log(String message)
    {
        Logger.log("Customer " + id + " " + message);
    }
    
    @Override
    protected JsonResponse awaitResponse()
        throws IOException, InterruptedException
    {
        JsonResponse response = super.awaitResponse();
        log(response);
        
        return response;
    }
    
    protected static final String getUUID(JsonResponse response)
    {
        return getUUID(response.getPayload());
    }
    
    protected static final String getUUID(JsonElement jsonElement)
    {
        return getAsString(jsonElement, "uuid");
    }
}
