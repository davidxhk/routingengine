package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.RoutingEngine.DEFAULT_ADMIN;
import static org.junit.Assume.assumeTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class RemoveAgentMethodTest extends AbstractMethodTest
{   
    protected static final String method = "remove_agent";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(50);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeAgentWithUUID(agentUUIDString);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.hasAssignedSupportRequest());
                
                assertFalse(agent.isAvailable());
                
                assertFalse(agent.isActivated());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                assertResponseDidNotSucceed(response);
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.hasAssignedAgent());
            }
        });
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid rainbow id")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(50);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeAgentWithRainbowId(rainbowId);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.hasAssignedSupportRequest());
                
                assertFalse(agent.isAvailable());
                
                assertFalse(agent.isActivated());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                assertResponseDidNotSucceed(response);
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.hasAssignedAgent());
            }
        });
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid or rainbow id")
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
                    .setArgument("uuid", "b50544fc-c7db-4c84-ae49-297c3676c796")
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
                    .setArgument("rainbow_id", new ArrayList<>())
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
                    .setArgument("rainbow_id", new HashMap<>())
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
                    .setArgument("rainbow_id", "hahaha test test")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing input")
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
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid or rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2 - Missing admin uuid")
    void test12()
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
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.1 - Unexpected arguments (valid uuid)")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(50);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.hasAssignedSupportRequest());
                
                assertFalse(agent.isAvailable());
                
                assertFalse(agent.isActivated());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                assertResponseDidNotSucceed(response);
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.hasAssignedAgent());
            }
        });
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.2 - Unexpected arguments (valid rainbow id)")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        agentUpdatesAvailability(agentUUIDString, true);
        
        agentTakesSupportRequest(agentUUIDString);
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customerWaitsForAgent(supportRequestUUIDString);
        
        TimeUnit.MILLISECONDS.sleep(50);
        
        assumeTrue(agentDidTakeSupportRequest(agentUUIDString, supportRequestUUIDString));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                assertFalse(agent.hasAssignedSupportRequest());
                
                assertFalse(agent.isAvailable());
                
                assertFalse(agent.isActivated());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                assertResponseDidNotSucceed(response);
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assertFalse(supportRequest.hasAssignedAgent());
            }
        });
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.4.1 - Malformed arguments: string")
    void test15()
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
    @DisplayName("Test 3.4.2 - Malformed arguments: empty string")
    void test16()
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
    @DisplayName("Test 3.4.3 - Malformed arguments: numbers")
    void test17()
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
    @DisplayName("Test 3.4.4 - Malformed arguments: json array")
    void test18()
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
    @DisplayName("Test 3.4.5 - Malformed arguments: invalid json object")
    void test19()
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
