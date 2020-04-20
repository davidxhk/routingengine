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
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentAvailabilityMethodTest extends AbstractMethodTest
{   
    protected static final String method = "update_agent_availability";
    
    @Test
    @DisplayName("Test 1.1.1 - Available true (valid uuid)")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentAvailabilityWithUUID(agentUUIDString, true);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.1.2 - Available true (valid rainbow id)")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentAvailabilityWithRainbowId(rainbowId, true);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertTrue(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.1 - Available false (valid uuid)")
    void test03()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentAvailabilityWithUUID(agentUUIDString, false);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.2 - Available false (valid rainbow id)")
    void test04()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentAvailabilityWithRainbowId(rainbowId, false);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
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
                    .setArgument("available", true)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.1 - Missing available (valid uuid)")
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1.2 - Missing available (valid rainbow id)")
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid available: json array (valid uuid)")
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
                    .setArgument("available", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid available: json array (valid rainbow id)")
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
                    .setArgument("available", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Invalid available: json object (valid uuid)")
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
                    .setArgument("available", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Invalid available: json object (valid rainbow id)")
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
                    .setArgument("available", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.5 - Invalid available: numbers (valid uuid)")
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
                    .setArgument("available", 12345)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.6 - Invalid available: numbers (valid rainbow id)")
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
                    .setArgument("available", 12345)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.7 - Invalid available: non-boolean string (valid uuid)")
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
                    .setArgument("available", "not true")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.8 - Invalid available: non-boolean string (valid rainbow id)")
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
                    .setArgument("available", "not true")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "available must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid or rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2.1 - Unexpected arguments (valid uuid)")
    void test24()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", false)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.2.2 - Unexpected arguments (valid rainbow id)")
    void test25()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("available", false)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.isAvailable());
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments: string")
    void test26()
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
    @DisplayName("Test 4.3.2 - Malformed arguments: empty string")
    void test27()
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
    @DisplayName("Test 4.3.3 - Malformed arguments: numbers")
    void test28()
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
    @DisplayName("Test 4.3.4 - Malformed arguments: json array")
    void test29()
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
    @DisplayName("Test 4.3.5 - Malformed arguments: invalid json object")
    void test30()
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
