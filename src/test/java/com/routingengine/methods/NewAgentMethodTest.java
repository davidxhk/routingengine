package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.RoutingEngine.DEFAULT_ADMIN;
import static com.routingengine.SupportRequest.Type;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewAgentMethodTest extends AbstractMethodTest
{   
    protected static final String method = "new_agent";
    
    @Test
    @DisplayName("Test 1.1 - Valid rainbow id and skills (using type index)")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        Agent[] agent = new Agent[1];
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                agent[0] = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), agent[0].ableToService(Type.of(entry.getKey())));
            }
        });
        
        agentGetsRemoved(agent[0].getUUID().toString());
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid rainbow id and skills (using type string)")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<String, Boolean> skills = Map.of("GENERAL_ENQUIRY", true, "CHECK_BILL", true);
        
        Agent[] agent = new Agent[1];
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                agent[0] = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<String, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), agent[0].ableToService(Type.of(entry.getKey())));
            }
        });
        
        agentGetsRemoved(agent[0].getUUID().toString());
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing rainbow id")
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
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.1 - Invalid rainbow id: json array")
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
                    .setArgument("rainbow_id", new ArrayList<>())
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2 - Invalid rainbow id: json object")
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
                    .setArgument("rainbow_id", new HashMap<>())
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.3 - Invalid rainbow id: already exists")
    void test06()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id already exists");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing skills")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
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
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Missing skill (using type index)")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(999, true, -1, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Missing skill (using type string)")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<String, Boolean> skills = Map.of("TEST", true, "hmmm", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.1 - Invalid skill ability (using type index)")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, String> skills = Map.of(1, "haha");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills 1 must be true or false");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.3.2 - Invalid skill ability (using type string)")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<String, String> skills = Map.of("GENERAL_ENQUIRY", "no");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills GENERAL ENQUIRY must be true or false");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.4 - Invalid new skills")
    void test12()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, false, 1, false, 2, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "new skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.5.1 - Invalid skills: number")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Integer skills = 2;
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skills)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.5.2 - Invalid skills: string")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final String skills = "zz";
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skills)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.5.3 - Invalid skills: json array")
    void test15()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final List<Integer> skills = List.of(1, 2, 3, 4);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skills)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test16()
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
                
                assertResponseHasErrorPayload(response, "rainbow id missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2 - Missing admin uuid")
    void test17()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skills)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "admin uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3 - Unexpected arguments")
    void test18()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        Agent[] agent = new Agent[1];
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skills)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                agent[0] = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), agent[0].ableToService(Type.of(entry.getKey())));
            }
        });
        
        agentGetsRemoved(agent[0].getUUID().toString());
    }
    
    @Test
    @DisplayName("Test 4.4.1 - Malformed arguments: string")
    void test19()
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
    void test20()
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
    void test21()
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
    void test22()
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
    void test23()
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
