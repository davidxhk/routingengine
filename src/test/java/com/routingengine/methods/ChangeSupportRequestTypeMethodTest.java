package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.SupportRequest;
import com.routingengine.SupportRequest.Type;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class ChangeSupportRequestTypeMethodTest extends AbstractMethodTest
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
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                changeSupportRequestType(supportRequestUUIDString, newType);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                SupportRequest supportRequest = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 1.2 - Valid uuid and type string")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String oldType = "CHECK_BILL";
        
        final String newType = "GENERAL_ENQUIRY";
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", oldType);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                changeSupportRequestType(supportRequestUUIDString, newType);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                SupportRequest supportRequest = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid missing");
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid not found");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing type")
    void test08()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type missing");
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid type: json array")
    void test09()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type invalid");
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
   @Test
    @DisplayName("Test 3.2.2 - Invalid type: json object")
    void test10()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type invalid");
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.3 - Invalid type: index out of bounds")
    void test11()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", 999)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type index out of bounds");
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 3.4 - Invalid type: invalid string")
    void test12()
        throws IOException, InterruptedException, ExecutionException
    {
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", 1);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", "blabla")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type string invalid");
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing input")
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "uuid missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2 - Unexpected arguments")
    void test14()
        throws IOException, InterruptedException, ExecutionException
    {
        final int oldType = 0;
        
        final int newType = 1;
        
        final String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", oldType);
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("uuid", supportRequestUUIDString)
                    .setArgument("type", newType)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                SupportRequest supportRequest = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(Type.of(newType), supportRequest.getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequestUUIDString);
    }
    
    @Test
    @DisplayName("Test 4.3.1 - Malformed arguments: string")
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
    @DisplayName("Test 4.3.2 - Malformed arguments: empty string")
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
    @DisplayName("Test 4.3.3 - Malformed arguments: numbers")
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
    @DisplayName("Test 4.3.4 - Malformed arguments: json array")
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
    @DisplayName("Test 4.3.5 - Malformed arguments: invalid json object")
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
