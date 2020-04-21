package com.routingengine.examples;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import com.routingengine.Logger;
import com.routingengine.client.LogClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class CustomerClientConnectionHandler extends LogClientConnectionHandler
{
    public int id;
    public int type;
    private Random random = null;
    private static final int MIN_TIMEOUT_MILLIS = 2000;
    private static final int MAX_TIMEOUT_MILLIS = 8000;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException
    {
        log("initialized!");
        
        randomSleep();
        
        newSupportRequest(
            "Customer " + id,
            "Customer" + id + "@gmail.com",
            type);
        
        JsonResponse response = awaitResponse();
        
        String supportRequestUUIDString = getSupportRequest(response).getUUID().toString();
        
        log("uuid -> " + supportRequestUUIDString);
        
        randomSleep();
        
        while (true) {
            waitForAgent(supportRequestUUIDString);
            
            response = awaitResponse();
            
            if (response.didSucceed())
                break;
            
            else
                ensureFailedResponseHasErrorPayload(response, "wait for agent timeout");
        }
        
        randomSleep();
        
        closeSupportRequest(supportRequestUUIDString);
        
        awaitResponse();
        
        exit();
    }
    
    protected final void randomSleep()
        throws InterruptedException
    {
        if (random == null)
            random = ThreadLocalRandom.current();
        
        int timeout = random.nextInt(MAX_TIMEOUT_MILLIS - MIN_TIMEOUT_MILLIS) + MIN_TIMEOUT_MILLIS;
        
        TimeUnit.MILLISECONDS.sleep(timeout);
    }
    
    @Override
    protected void log(String message)
    {
        Logger.log("Customer " + id + " " + message);
    }
}
