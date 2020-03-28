package com.routingengine.client;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonUtils.getAsString;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonElement;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class CustomerClientConnectionHandler extends ClientConnectionHandler
{
    public Random random;
    public int i;
    
    @Override
    public void runMainLoop()
        throws IOException, InterruptedException, EndConnectionException
    {
        customerLog("initialized!");
        
        randomSleep(10);
        
        customerLog("creating new support request");
        JsonResponse response = newSupportRequest("client "+i, "client"+i+"@gmail.com", i%3);
        String supportRequestUUIDString = getUUID(response);
        customerLog("uuid -> " + supportRequestUUIDString);
        
        randomSleep(10);
        
        while (true) {
            customerLog("waiting for agent");
            response = waitForAgent(supportRequestUUIDString);
            customerLog(response);
            
            if (response.didSucceed())
                break;
        }
        
        randomSleep(10);
        
        customerLog("closing support request");
        response = closeSupportRequest(supportRequestUUIDString);
        customerLog(response);
        
        exit();
    }
    
    public final void randomSleep(int timeout)
        throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(random.nextInt(timeout));
    }
    
    public final void customerLog(JsonResponse jsonResponse)
    {
        customerLog(jsonResponse.toString());
    }
    
    public final void customerLog(String message)
    {
        log("Customer " + i + " " + message);
    }
    
    public static final String getUUID(JsonResponse response)
    {
        return getUUID(response.getPayload());
    }
    
    public static final String getUUID(JsonElement jsonElement)
    {
        return getAsString(jsonElement, "uuid");
    }
}
