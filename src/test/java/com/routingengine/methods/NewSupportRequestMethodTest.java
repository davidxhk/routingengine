package com.routingengine.methods;

import static com.routingengine.SupportRequest.Type;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.routingengine.SupportRequest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public class NewSupportRequestMethodTest extends AbstractMethodTest
{   
    protected static final String method = "new_support_request";
    
    @Test
    @DisplayName("Test 1.1 - Name, email, and type index")
    void test01()
        throws IOException, InterruptedException, ExecutionException
    {
        final String name = "bob";
        
        final String email = "bob@abc.com";
        
        final int type = 1;
        
        SupportRequest[] supportRequest = new SupportRequest[1];
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newSupportRequest(name, email, type);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                supportRequest[0] = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(name, supportRequest[0].getUser().getName());
                
                assertEquals(email, supportRequest[0].getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest[0].getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequest[0].getUUID().toString());
    }
    
    @Test
    @DisplayName("Test 1.2 - Name, email, and type string")
    void test02()
        throws IOException, InterruptedException, ExecutionException
    {
        final String name = "bob";
        
        final String email = "bob@abc.com";
        
        final String type = "GENERAL_ENQUIRY";
        
        SupportRequest[] supportRequest = new SupportRequest[1];
        
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newSupportRequest(name, email, type);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                supportRequest[0] = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(name, supportRequest[0].getUser().getName());
                
                assertEquals(email, supportRequest[0].getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest[0].getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequest[0].getUUID().toString());
    }
    
    @Test
    @DisplayName("Test 2.1 - Missing name")
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
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "name missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.1 - Invalid name: json array")
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
                    .setArgument("name", new ArrayList<>())
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "name invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 2.2.2 - Invalid name: json object")
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
                    .setArgument("name", new HashMap<>())
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "name invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.1 - Missing email")
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
                    .setArgument("name", "bob")
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "email missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2.1 - Invalid email: json array")
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
                    .setArgument("name", "bob")
                    .setArgument("email", new ArrayList<>())
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "email invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 3.2.2 - Invalid email: json object")
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
                    .setArgument("name", "bob")
                    .setArgument("email", new HashMap<>())
                    .setArgument("type", 1)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "email invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.1 - Missing type")
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
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2.1 - Invalid type: json array")
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
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", new ArrayList<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.2.2 - Invalid type: json object")
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
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", new HashMap<>())
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.3 - Invalid type: index out of bounds")
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
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", 999)
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type index out of bounds");
            }
        });
    }
    
    @Test
    @DisplayName("Test 4.4 - Invalid type: invalid string")
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
                    .setArgument("name", "bob")
                    .setArgument("email", "bob@abc.com")
                    .setArgument("type", "blabla")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "type string invalid");
            }
        });
    }
    
    @Test
    @DisplayName("Test 5.1 - Missing input")
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
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                assertResponseHasErrorPayload(response, "name missing");
            }
        });
    }
    
    @Test
    @DisplayName("Test 5.2 - Unexpected arguments")
    void test15()
        throws IOException, InterruptedException, ExecutionException
    {
        final String name = "bob";
        
        final String email = "bob@abc.com";
        
        final int type = 1;
        
        SupportRequest[] supportRequest = new SupportRequest[1];
                
        execute(new ClientConnectionHandler() {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                new JsonRequest()
                    .setMethod(method)
                    .setArgument("name", name)
                    .setArgument("email", email)
                    .setArgument("type", type)
                    .setArgument("something", "something?")
                    .writeTo(jsonWriter);
                
                JsonResponse response = awaitResponse();
                
                assertEquals(method, response.getMethod());
                
                supportRequest[0] = assertResponseHasSupportRequestPayload(response);
                
                assertEquals(name, supportRequest[0].getUser().getName());
                
                assertEquals(email, supportRequest[0].getUser().getEmail());
                
                assertEquals(Type.of(type), supportRequest[0].getType());
            }
        });
        
        supportRequestGetsRemoved(supportRequest[0].getUUID().toString());
    }
    
    
    @Test
    @DisplayName("Test 5.3.1 - Malformed arguments: string")
    void test16()
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
    @DisplayName("Test 5.3.2 - Malformed arguments: empty string")
    void test17()
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
    @DisplayName("Test 5.3.3 - Malformed arguments: numbers")
    void test18()
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
    @DisplayName("Test 5.3.4 - Malformed arguments: json array")
    void test19()
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
    @DisplayName("Test 5.3.5 - Malformed arguments: invalid json object")
    void test20()
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
