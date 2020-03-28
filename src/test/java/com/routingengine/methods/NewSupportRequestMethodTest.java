package com.routingengine.methods;

import static com.routingengine.SupportRequest.Type;
import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.routingengine.SupportRequest;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewSupportRequestMethodTest
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
    @DisplayName("Test 1.1 - Name, email, and type index")
    void test01()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                String name = "bob";
                String email = "bob@abc.com";
                int type = 1;
                
                JsonResponse response = newSupportRequest(name, email, type);
                
                assertEquals("new_support_request", response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(name, supportRequest.getUser().getName());
                
                assertEquals(email, supportRequest.getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest.getType());
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 1.2 - Name, email, and type string")
    void test02()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                String name = "bob";
                String email = "bob@abc.com";
                String type = "GENERAL_ENQUIRY";
                
                JsonResponse response = newSupportRequest(name, email, type);
                
                assertEquals("new_support_request", response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(name, supportRequest.getUser().getName());
                
                assertEquals(email, supportRequest.getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest.getType());
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 1.3 - Name, email, type and address")
    void test03()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                String name = "bob";
                String email = "bob@abc.com";
                String type = "GENERAL_ENQUIRY";
                String address = "127.0.0.1";
                
                JsonResponse response = newSupportRequest(name, email, type, address);
                
                assertEquals("new_support_request", response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(name, supportRequest.getUser().getName());
                
                assertEquals(email, supportRequest.getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest.getType());
                
                assertEquals(address, supportRequest.getAddress().getHostAddress().toString());
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing name")
    void test04()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("name missing", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid name case 1")
    void test05()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", new ArrayList<>())
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("name invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid name case 2")
    void test06()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", new HashMap<>())
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("name invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing email")
    void test07()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("email missing", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid email case 1")
    void test08()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", new ArrayList<>())
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("email invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid email case 2")
    void test09()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", new HashMap<>())
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("email invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing type")
    void test10()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type missing", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 4.2.1 - Invalid type case 1")
    void test11()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 4.2.2 - Invalid type case 2")
    void test12()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 4.3 - Invalid type case 3")
    void test13()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 999)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type index out of bounds", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 4.4 - Invalid type case 4")
    void test14()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", "blabla")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type string invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 5.1.1 - Invalid address case 1")
    void test15()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .setArgument("address", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("address invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 5.1.2 - Invalid address case 2")
    void test16()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .setArgument("address", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("address invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 5.1.3 - Invalid address case 3")
    void test17()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .setArgument("address", "adsflasdfmlsdf")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("address invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 5.1.4 - Invalid address case 4")
    void test18()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .setArgument("address", "999.999.999.999")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("address invalid", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.1 - Missing input")
    void test19()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod("new_support_request")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("name missing", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.2 - Unexpected arguments")
    void test20()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                String name = "bob";
                String email = "bob@abc.com";
                int type = 1;
                
                new JsonRequest()
                    .setMethod("new_support_request")
                    .setArgument("name", name)
                    .setArgument("email", email)
                    .setArgument("type", 1)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(name, supportRequest.getUser().getName());
                
                assertEquals(email, supportRequest.getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest.getType());
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.3.1 - Malformed arguments case 1")
    void test21()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString("new_support_request \"testtest\"");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.3.2 - Malformed arguments case 2")
    void test22()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString("new_support_request []");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.3.3 - Malformed arguments case 3")
    void test23()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString("new_support_request ;!/");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        client.run();
    }
    
    @Test
    @DisplayName("Test 6.3.4 - Malformed arguments case 4")
    void test24()
        throws IOException
    {
        client.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                jsonWriter.writeString("new_support_request }}}}");
                jsonWriter.flush();
                
                JsonResponse response = awaitResponse();
                
                assertEquals("new_support_request", response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("malformed arguments", error);
            }
        });
        
        client.run();
    }
}
