package com.routingengine.test.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.routingengine.Logger;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class TestTakeSupportRequestMethod
{   
    private static Client client;
    private static final String hostname = "localhost";
    private static final int port = 50000;
    
    @BeforeAll
    static void setUpBeforeClass()
        throws Exception
    {
        client = new Client(hostname, port);
    }
    
    @AfterAll
    static void tearDownAfterClass()
        throws Exception
    {
        client.close();
    }
    
    @Test
    void test()
        throws IOException
    {
        Logger.log("hello from test");
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            protected void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                Logger.log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
    
    @Test
    void test2()
        throws IOException
    {
        Logger.log("hello from test2");
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            protected void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                Logger.log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
    
    @Test
    void test3()
        throws IOException
    {
        Logger.log("hello from test3");
        
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            protected void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = ping();
                Logger.log(response.toString());
                
                assertEquals("pong", castToString(response.getPayload()));
            }  
        });
        
        client.run();
    }
}
