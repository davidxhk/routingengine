package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.RoutingEngine.DEFAULT_ADMIN;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ActivateAgentMethodTest extends AbstractMethodTest
{   
    protected static final String method = "activate_agent";
    
    @Test
    @DisplayName("Test 1.1.1 - Activate true (using uuid)")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentGetsActivated(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                activateAgentWithUUID(agentUUIDString, true);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.1.2 - Activate true (using rainbow id)")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentGetsActivated(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                activateAgentWithRainbowId(rainbowId, true);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.1 - Activate false (using uuid)")
    void test03()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                activateAgentWithUUID(agentUUIDString, false);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.2 - Activate false (using rainbow id)")
    void test04()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                activateAgentWithRainbowId(rainbowId, false);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid or rainbow id")
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
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid or rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid: json array")
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
                    .setArgument("uuid", new ArrayList<>())
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid: json object")
    void test07()
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
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid: non-conforming string")
    void test08()
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
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.4 - Invalid uuid: unknown uuid")
    void test09()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "b50544fc-c7db-4c84-ae49-297c3676c796")
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.3.1 - Invalid rainbow id: json array")
    void test10()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", new ArrayList<>())
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.3.2 - Invalid rainbow id: json object")
    void test11()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", new HashMap<>())
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.3.3 - Invalid rainbow id: unknown rainbow id")
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
                    .setArgument("rainbow_id", "hahaha test test")
                    .setArgument("activate", true)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.1 - Missing activate (using uuid)")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1.2 - Missing activate (using rainbow id)")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid activate: json array (using uuid)")
    void test15()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", new ArrayList<>())
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid activate: json array (using rainbow id)")
    void test16()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", new ArrayList<>())
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Invalid activate: json object (using uuid)")
    void test17()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", new HashMap<>())
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Invalid activate: json object (using rainbow id)")
    void test18()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", new HashMap<>())
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.5 - Invalid activate: numbers (using uuid)")
    void test19()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", 12345)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.6 - Invalid activate: numbers (using rainbow id)")
    void test20()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", 12345)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.7 - Invalid activate: non-boolean string (using uuid)")
    void test21()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", "not true")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.8 - Invalid activate: non-boolean string (using rainbow id)")
    void test22()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", "not true")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "activate must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test23()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid or rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2 - Missing admin uuid")
    void test24()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", false)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "admin uuid missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Unexpected arguments (using uuid)")
    void test25()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("activate", false)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Unexpected arguments (using rainbow id)")
    void test26()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("activate", false)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isActivated());
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.4.1 - Malformed arguments: string")
    void test27()
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
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.4.2 - Malformed arguments: empty string")
    void test28()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + "");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.4.3 - Malformed arguments: numbers")
    void test29()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " 1234");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.4.4 - Malformed arguments: json array")
    void test30()
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
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.4.5 - Malformed arguments: invalid json object")
    void test31()
        throws IOException
    {
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " {{{{");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
}
