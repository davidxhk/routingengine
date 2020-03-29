package com.routingengine.methods;

import static com.routingengine.SupportRequest.Type;
import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;
import com.routingengine.MethodTestBase;
import com.routingengine.SupportRequest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ChangeSupportRequestTypeMethodTest extends MethodTestBase
{   
    protected static final String method = "change_support_request_type";
    
    @Test
    @DisplayName("Test 1.1 - Valid uuid and type index")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final int oldType = 0;
        final int newType = 1;
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", oldType);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = changeSupportRequestType(supportRequestUUIDString, newType);
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid uuid and type string")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String oldType = "CHECK_BILL";
        final String newType = "GENERAL_ENQUIRY";
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", oldType);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = changeSupportRequestType(supportRequestUUIDString, newType);
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing uuid")
    void test03()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid missing", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid uuid case 1")
    void test04()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new ArrayList<>())
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid uuid case 2")
    void test05()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", new HashMap<>())
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 2.2.3 - Invalid uuid case 3")
    void test06()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", "hahaha test test")
                    .setArgument("type", 1)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("uuid invalid", error);
            }
        });
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing type")
    void test07()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type missing", error);
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid type case 1")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", new ArrayList<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type invalid", error);
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid type case 2")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", new HashMap<>())
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type invalid", error);
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3 - Invalid type case 3")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", 999)
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type index out of bounds", error);
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.4 - Invalid type case 4")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", "blabla")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertFalse(response.didSucceed());
                
                String error = castToString(response.getPayload());
                
                assertEquals("type string invalid", error);
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
        
    @Test
    @DisplayName("Test 4.1 - Missing input")
    void test12()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
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
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 4.2 - Unexpected arguments")
    void test13()
        throws IOException, InterruptedException, ExecutionException
    {
        final int oldType = 0;
        final int newType = 1;
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", oldType);
        
        customer.setConnectionHandler(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", newType)
                    .setArgument("something", "something?")
                    .writeSafe(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertTrue(response.didSucceed());
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                assertNotNull(payload);
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        customer.run();
        
        removeSupportRequest(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments case 1")
    void test14()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
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
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 4.3.2 - Malformed arguments case 2")
    void test15()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
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
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 4.3.3 - Malformed arguments case 3")
    void test16()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
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
        
        customer.run();
    }
    
    @Test
    @DisplayName("Test 4.3.4 - Malformed arguments case 4")
    void test17()
        throws IOException
    {
        customer.setConnectionHandler(new ClientConnectionHandler() {
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
        
        customer.run();
    }
}
