package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assume.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.MethodTestBase;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentAvailabilityMethodTest extends MethodTestBase
{   
    protected static final String method = "update_agent_availability";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid and available true")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assumeNotNull(payload);
                
                Agent agent = Agent.fromJson(payload);
                
                assumeFalse(agent.isAvailable());
                
                response = updateAgentAvailability(agentUUIDString, true);
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                agent = Agent.fromJson(payload);
                
                assumeTrue(agent.isAvailable());
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid uuid and available false")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentAvailability(agentUUIDString, true);
                
                assumeTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assumeNotNull(payload);
                
                Agent agent = Agent.fromJson(payload);
                
                assumeTrue(agent.isAvailable());
                
                response = updateAgentAvailability(agentUUIDString, false);
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                agent = Agent.fromJson(payload);
                
                assumeFalse(agent.isAvailable());
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid")
    void test03()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("available", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid missing", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid case 1")
    void test04()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new ArrayList<>())
                    .setArgument("available", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid case 2")
    void test05()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new HashMap<>())
                    .setArgument("available", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid case 3")
    void test06()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "hahaha test test")
                    .setArgument("available", true)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing available")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
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
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("available missing", error);
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid available case 1")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("available invalid", error);
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid available case 2")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("available invalid", error);
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Invalid available case 3")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", 12345)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("available must be true or false", error);
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Invalid available case 4")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", "not true")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("available must be true or false", error);
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test12()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid missing", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 4.2 - Unexpected arguments")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assumeNotNull(payload);
                
                Agent agent = Agent.fromJson(payload);
                
                assumeFalse(agent.isAvailable());
                
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("available", true)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                agent = Agent.fromJson(payload);
                
                assumeTrue(agent.isAvailable());
            }
        });
        
        agent.run();
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments case 1")
    void test14()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " \"testtest\"");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Malformed arguments case 2")
    void test15()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " []");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 4.3.3 - Malformed arguments case 3")
    void test16()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " ;!/");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        agent.run();
    }
    
    @Test
    @DisplayName("Test 4.3.4 - Malformed arguments case 4")
    void test17()
        throws IOException
    {
        agent.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString(method + " }}}}");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        agent.run();
    }
}
