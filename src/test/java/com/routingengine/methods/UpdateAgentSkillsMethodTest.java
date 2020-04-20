package com.routingengine.methods;

import static com.routingengine.RoutingEngine.DEFAULT_ADMIN;
import static com.routingengine.SupportRequest.Type;
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
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class UpdateAgentSkillsMethodTest extends AbstractMethodTest
{   
    protected static final String method = "update_agent_skills";
    
    @Test
    @DisplayName("Test 1.1.1 - Valid skills (using type index and uuid)")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(1, true, 2, true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<Integer, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.1.2 - Valid skills (using type index and rainbow id)")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(1, true, 2, true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<Integer, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.1 - Valid skills (using type string and uuid)")
    void test03()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, Boolean> skillUpdate = Map.of("GENERAL_ENQUIRY", true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<String, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2.2 - Valid skills (using type string and rainbow id)")
    void test04()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, Boolean> skillUpdate = Map.of("GENERAL_ENQUIRY", true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<String, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
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
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "rainbow id not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.1 - Missing skills (using uuid)")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
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
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.1.2 - Missing skills (using rainbow id)")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
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
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Missing skill (using type index and uuid)")
    void test15()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(999, true, -1, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Missing skill (using type index and rainbow id)")
    void test16()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(999, true, -1, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.3 - Missing skill (using type string and uuid)")
    void test17()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, Boolean> skillUpdate = Map.of("TEST", true, "hmmm", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.4 - Missing skill (using type string and rainbow id)")
    void test18()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, Boolean> skillUpdate = Map.of("TEST", true, "hmmm", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.1 - Invalid skill ability (using type index and uuid)")
    void test19()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, String> skillUpdate = Map.of(1, "haha");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills 1 must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.2 - Invalid skill ability (using type index and rainbow id)")
    void test20()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, String> skillUpdate = Map.of(1, "haha");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills 1 must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.3 - Invalid skill ability (using type string and uuid)")
    void test21()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, String> skillUpdate = Map.of("GENERAL_ENQUIRY", "no");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills GENERAL ENQUIRY must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3.4 - Invalid skill ability (using type string and rainbow id)")
    void test22()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<String, String> skillUpdate = Map.of("GENERAL_ENQUIRY", "no");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills GENERAL ENQUIRY must be true or false");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.4.1 - Invalid new skills (using uuid)")
    void test23()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(0, false, 1, false, 2, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "new skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.4.2 - Invalid new skills (using rainbow id)")
    void test24()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(0, false, 1, false, 2, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "new skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.1 - Invalid skills: number (using uuid)")
    void test25()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Integer skillUpdate = 2;
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.2 - Invalid skills: number (using rainbow id)")
    void test26()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Integer skillUpdate = 2;
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.3 - Invalid skills: string (using uuid)")
    void test27()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final String skillUpdate = "zz";
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.4 - Invalid skills: string (using rainbow id)")
    void test28()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final String skillUpdate = "zz";
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.5 - Invalid skills: json array (using uuid)")
    void test29()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final List<Integer> skillUpdate = List.of(1, 2, 3, 4);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.5.6 - Invalid skills: json array (using rainbow id)")
    void test30()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final List<Integer> skillUpdate = List.of(1, 2, 3, 4);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skillUpdate)
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test31()
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
    void test32()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(1, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skillUpdate)
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
    void test33()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(1, true, 2, true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<Integer, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", agentUUIDString)
                    .setArgument("skills", skillUpdate)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Unexpected arguments (using rainbow id)")
    void test34()
        throws IOException, InterruptedException, ExecutionException
    {
        final String rainbowId = "rainbow_agent";
        
        final Map<Integer, Boolean> skills = Map.of(0, true);
        
        final String agentUUIDString = generateNewAgent(rainbowId, skills);
        
        final Map<Integer, Boolean> skillUpdate = Map.of(1, true, 2, true);
        
        final Map<Type, Boolean> newSkills = new HashMap<>();
        
        for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        for (Map.Entry<Integer, Boolean> entry : skillUpdate.entrySet())
            newSkills.put(Type.of(entry.getKey()), entry.getValue());
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("rainbow_id", rainbowId)
                    .setArgument("skills", skillUpdate)
                    .setArgument("something", "something?")
                    .setArgument("admin_uuid", DEFAULT_ADMIN)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Type, Boolean> entry : newSkills.entrySet())
                    assertEquals(entry.getValue(), agent.ableToService(entry.getKey()));
            }
        });
        
        agentGetsRemoved(agentUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.4.1 - Malformed arguments: string")
    void test35()
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
    void test36()
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
    void test37()
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
    void test38()
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
    void test39()
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
