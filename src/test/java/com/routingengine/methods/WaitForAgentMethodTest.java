package com.routingengine.methods;

import static com.routingengine.Logger.log;
import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class WaitForAgentMethodTest
{   
    private static Client client, client2;
    private static final String hostname = "localhost";
    private static final int port = 50000;
    private volatile String supportRequestUUIDString, agentUUIDString;
    private Thread client2Thread;
    
    @BeforeAll
    static void setUpBeforeClass()
        throws Exception
    {
        client = new Client(hostname, port);
        client2 = new Client(hostname, port);
    }
    
    @AfterAll
    static void tearDownAfterClass()
        throws Exception
    {
        client.close();
        client2.close();
    }
    
    @BeforeEach
    void setUpBeforeEach()
        throws IOException
    {
        client2.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newSupportRequest("bob", "bob@abc.com", 1);
                JsonObject payload = castToJsonObject(response.getPayload());
                supportRequestUUIDString = getAsString(payload, "uuid");
                
                response = newAgent(Map.of(1, true));
                payload = castToJsonObject(response.getPayload());
                agentUUIDString = getAsString(payload, "uuid");
                updateAgentAvailability(agentUUIDString, true);
                takeSupportRequest(agentUUIDString);
            }
        });
        
        client2Thread = new Thread(client2);
        client2Thread.run();
    }
    
    @AfterEach
    void tearDownAfterEach()
        throws IOException
    {
        client2Thread.interrupt();
        client2.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                closeSupportRequest(supportRequestUUIDString);
                removeAgent(agentUUIDString);
            }
        });
        
        client2Thread = new Thread(client2);
        client2Thread.run();
        
    }
    
    @Test
    void test01()
        throws IOException
    {
        log("test01 supportRequestUUID: " + supportRequestUUIDString + ", agentUUID: " + agentUUIDString);
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
    
    @Test
    void test02()
        throws IOException
    {
        log("test02 supportRequestUUID: " + supportRequestUUIDString + ", agentUUID: " + agentUUIDString);
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
    
    @Test
    void test03()
        throws IOException
    {
        log("test03 supportRequestUUID: " + supportRequestUUIDString + ", agentUUID: " + agentUUIDString);
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
}
