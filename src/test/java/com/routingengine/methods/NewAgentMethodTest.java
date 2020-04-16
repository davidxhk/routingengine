package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Test 1.1 - Skill with type index")
    void test01()
        throws IOException
    {
        Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
                
                removeAgent(agent.getUUID().toString());
            }
        });
    }
    
    @Test
    @DisplayName("Test 1.2 - Skill with type string")
    void test02()
        throws IOException
    {
        final Map<String, Boolean> skills = Map.of("GENERAL_ENQUIRY", true, "CHECK_BILL", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<String, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
                
                removeAgent(agent.getUUID().toString());
            }
        });
    }
    
    @Test
    @DisplayName("Test 1.3 - Skill and address")
    void test03()
        throws IOException
    {
        final Map<String, Boolean> skills = Map.of("GENERAL_ENQUIRY", true, "CHECK_BILL", true);
        final String address = "127.0.0.1";
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills, address);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<String, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
                
                assertEquals(address, agent.getAddress().getHostAddress());
                
                removeAgent(agent.getUUID().toString());
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.1.1 - Missing skill case 1")
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.1.2 - Missing skill case 2")
    void test05()
        throws IOException
    {
        final Map<Integer, Boolean> skills = Map.of(999, true, -1, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.1.3 - Missing skill case 3")
    void test06()
        throws IOException
    {
        final Map<String, Boolean> skills = Map.of("TEST", true, "hmmm", true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "valid skill missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid skills case 1")
    void test07()
        throws IOException
    {
        final Map<Integer, String> skills = Map.of(1, "haha");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills 1 must be true or false");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid skills case 2")
    void test08()
        throws IOException
    {
        final Map<String, String> skills = Map.of("GENERAL_ENQUIRY", "no");
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills GENERAL_ENQUIRY must be true or false");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid skills case 3")
    void test09()
        throws IOException
    {
        final Map<Integer, Boolean> skills = Map.of(0, false, 1, false, 2, false);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "new skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.4 - Invalid skills case 4")
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
                    .setArgument("skills", 2)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.5 - Invalid skills case 5")
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
                    .setArgument("skills", "zz")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.6 - Invalid skills case 6")
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
                    .setArgument("skills", List.of(1, 2, 3, 4))
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.1 - Invalid address case 1")
    void test13()
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
                    .setArgument("address", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "address invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.2 - Invalid address case 2")
    void test14()
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
                    .setArgument("address", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "address invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.3 - Invalid address case 3")
    void test15()
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
                    .setArgument("address", "adsflasdfmlsdf")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "address invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1.4 - Invalid address case 4")
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
                    .setArgument("skills", Map.of(1, true, 2, true))
                    .setArgument("address", "999.999.999.999")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "address invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test17()
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
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "skills missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2 - Unexpected arguments")
    void test18()
        throws IOException, InterruptedException, ExecutionException
    {
        Map<Integer, Boolean> skills = Map.of(1, true, 2, true);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("skills", skills)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseDidSucceed(response);
                
                Agent agent = assertResponseHasAgentPayload(response);
                
                for (Map.Entry<Integer, Boolean> entry : skills.entrySet())
                    assertEquals(entry.getValue(), 
                        agent.ableToService(Type.of(entry.getKey())));
                
                removeAgent(agent.getUUID().toString());
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments case 1")
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
                
                assertResponseDidNotSucceed(response);
                
                assertResponseHasErrorPayload(response, "malformed arguments");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Malformed arguments case 2")
    void test20()
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
    void test21()
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
    void test22()
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
