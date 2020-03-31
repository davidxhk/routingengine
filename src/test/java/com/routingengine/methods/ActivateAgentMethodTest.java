package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.AbstractMethodTest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ActivateAgentMethodTest extends AbstractMethodTest
{   
    protected static final String method = "activate_agent";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid and activate true")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agentGetsActivated(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = activateAgent(agentUUIDString, true);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isActivated());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid uuid and activate false")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = activateAgent(agentUUIDString, false);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isActivated());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid")
    void test03()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("activate", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid case 1")
    void test04()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new ArrayList<>())
                    .setArgument("activate", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid case 2")
    void test05()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new HashMap<>())
                    .setArgument("activate", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid case 3")
    void test06()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "hahaha test test")
                    .setArgument("activate", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing activate")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "activate missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid activate case 1")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid activate case 2")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Invalid activate case 3")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", 12345)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Invalid activate case 4")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", "not true")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test12()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2 - Unexpected arguments")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agentGetsActivated(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", true)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isActivated());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments case 1")
    void test14()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " \"testtest\"");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Malformed arguments case 2")
    void test15()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " []");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3.3 - Malformed arguments case 3")
    void test16()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " ;!/");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3.4 - Malformed arguments case 4")
    void test17()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " }}}}");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
}
