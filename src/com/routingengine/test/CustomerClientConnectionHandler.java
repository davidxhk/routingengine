package com.routingengine.test;

import static com.routingengine.json.JsonUtils.getAsString;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.google.gson.JsonElement;
import com.routingengine.Logger;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class CustomerClientConnectionHandler extends ClientConnectionHandler
{
    public Random random;
    public int i;
    
    @Override
    protected void runMainLoop()
        throws IOException, InterruptedException
    {
        clientLog("initialized!");
        
        randomSleep(10);
        
        clientLog("creating new support request");
        JsonResponse response = newSupportRequest("client "+i, "client"+i+"@gmail.com", i%3);
        String supportRequestUUIDString = getUUID(response);
        clientLog("uuid -> " + supportRequestUUIDString);
        
        randomSleep(10);
        
        clientLog("waiting for agent");
        response = waitForAgent(supportRequestUUIDString);
        clientLog(response);
        
        randomSleep(10);
        
        clientLog("closing support request");
        response = closeSupportRequest(supportRequestUUIDString);
        clientLog(response);
    }
    
    public final void randomSleep(int timeout)
        throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(random.nextInt(timeout));
    }
    
    public final void clientLog(JsonResponse jsonResponse)
    {
        clientLog(jsonResponse.toString());
    }
    
    public final void clientLog(String message)
    {
        Logger.log("Client " + i + " " + message);
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
