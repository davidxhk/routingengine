package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.Agent;
import com.routingengine.AbstractMethodTest;
import com.routingengine.SupportRequest.Type;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentSkillsMethodTest extends AbstractMethodTest
{   
    protected static final String method = "update_agent_skills";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid and skill with type index")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid uuid and skill with type string")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(1, true));
        
        final Map<String, Boolean> skills = Map.of("GENERAL_ENQUIRY", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<String, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
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
                    .setArgument("skills", Map.of("GENERAL_ENQUIRY", true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "uuid invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.1 - Missing skill case 1")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
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
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1.2 - Missing skill case 2")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, Map.of(999, true, -1, true));
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1.3 - Missing skill case 3")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, Map.of("TEST", true, "hmmm", true));
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid skills case 1")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, Map.of(1, "haha"));
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills 1 must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid skills case 2")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, Map.of("GENERAL_ENQUIRY", "no"));
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills GENERAL_ENQUIRY must be true or false");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Invalid skills case 3")
    void test12()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentSkills(agentUUIDString, Map.of(0, false, 1, false, 2, false));
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "new skills invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Invalid skills case 4")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", 2)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.5 - Invalid skills case 5")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", "zz")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.6 - Invalid skills case 6")
    void test15()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", List.of(1, 2, 3, 4))
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test16()
        throws IOException, InterruptedException, ExecutionException
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
    void test17()
        throws IOException, InterruptedException, ExecutionException
    {
        final String agentUUIDString = generateNewAgent(Map.of(0, true));
        
        Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", skills)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
            }
        });
        
        removeAgent(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments case 1")
    void test18()
        throws IOException, InterruptedException, ExecutionException
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
    void test19()
        throws IOException, InterruptedException, ExecutionException
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
    void test20()
        throws IOException, InterruptedException, ExecutionException
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
    void test21()
        throws IOException, InterruptedException, ExecutionException
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
