package com.routingengine.methods;

import static org.junit.jupiter.api.Assertions.*;
import static com.routingengine.json.JsonUtils.*;
import static org.junit.Assume.*;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import com.google.gson.JsonObject;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;
import com.routingengine.server.Server;


public abstract class AbstractMethodTest
{
    private static final String hostname = "localhost";
    private static final int port = 50000;
    private static final int THREAD_POOL_SIZE = 100;
    protected static ExecutorService executor;
    protected static Thread serverThread;
    protected static Client client;
    protected static String method;
    
    @BeforeAll
    protected static final void setUpBeforeClass()
        throws Exception
    {
        executor = newFixedThreadPool(THREAD_POOL_SIZE);
        
        serverThread = new Thread(new Server(hostname, port));
        serverThread.start();
        
        TimeUnit.MILLISECONDS.sleep(50);
        
        client = new Client(hostname, port); 
    }
    
    @AfterAll
    protected static final void tearDownAfterClass()
        throws Exception
    {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                exit();
            }
        });
        
        serverThread.interrupt();
        serverThread.join();
    }
    
    protected static final void execute(ClientConnectionHandler connectionHandler)
        throws IOException
    {
        client.setConnectionHandler(connectionHandler);
        
        client.run();
    }
    
    private static final void executeInNewClient(ClientConnectionHandler connectionHandler)
        throws IOException
    {
        Client client = new Client(hostname, port);
        
        client.setConnectionHandler(new ClientConnectionHandler ()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                connectionHandler.connect(socket);
                
                try {
                    connectionHandler.runMainLoop();
                }
                
                finally {
                    exit();
                }
            }
        });
        
        executor.execute(client);
    }
    
    protected static final void assumeResponseDidSucceed(JsonResponse response)
    {
        String error = "assumed " + response.getMethod() + " would succeed, instead got " + response.getPayload();
        
        assumeTrue(error, response.didSucceed());
    }
    
    protected static final void assumeResponseDidNotSucceed(JsonResponse response)
    {
        String error = "assumed " + response.getMethod() + " would not succeed, instead got " + response.getPayload();
        
        assumeFalse(error, response.didSucceed());
    }
    
    protected static final Agent assumeResponseHasAgentPayload(JsonResponse response)
    {
        assumeResponseDidSucceed(response);
        
        Agent agent = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
            
            agent = Agent.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            assumeNoException(exception);
        }
        
        assumeNotNull(agent);
        
        return agent;
    }
    
    protected static final SupportRequest assumeResponseHasSupportRequestPayload(JsonResponse response)
    {
        assumeResponseDidSucceed(response);
        
        SupportRequest supportRequest = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
            
            supportRequest = SupportRequest.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            assumeNoException(exception);
        }
        
        assumeNotNull(supportRequest);
        
        return supportRequest;
    }
    
    protected static final void assumeResponseHasErrorPayload(JsonResponse response, String error)
    {
        assumeResponseDidNotSucceed(response);
        
        String payload = null;
        
        try {
            payload = castToString(response.getPayload());
        }
        
        catch (IllegalArgumentException exception) {
            assumeNoException(exception);
        }
        
        assumeNotNull(payload);
        
        assumeTrue(error.equals(payload));
    }
    
    protected static final void assertResponseDidSucceed(JsonResponse response)
    {
        if (!response.didSucceed())
            fail("expected " + response.getMethod() + " to succeed, instead got " + response.getPayload());
    }
    
    protected static final void assertResponseDidNotSucceed(JsonResponse response)
    {
        if (response.didSucceed())
            fail("expected " + response.getMethod() + " to fail, instead got " + response.getPayload());
    }
    
    protected static final Agent assertResponseHasAgentPayload(JsonResponse response)
    {
        assertResponseDidSucceed(response);
        
        Agent agent = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
            
            agent = Agent.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            fail(exception);
        }
        
        assertNotNull(agent);
        
        return agent;
    }
    
    protected static final SupportRequest assertResponseHasSupportRequestPayload(JsonResponse response)
    {
        assertResponseDidSucceed(response);
        
        SupportRequest supportRequest = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
            
            supportRequest = SupportRequest.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            fail(exception);
        }
        
        assertNotNull(supportRequest);
        
        return supportRequest;
    }
    
    protected static final void assertResponseHasErrorPayload(JsonResponse response, String error)
    {
        assertResponseDidNotSucceed(response);
        
        String payload = null;
        
        try {
            payload = castToString(response.getPayload());
        }
        
        catch (IllegalArgumentException exception) {
            fail(exception);
        }
        
        assertNotNull(payload);
        
        assertEquals(error, payload);
    }
    
    protected static final String generateNewSupportRequest(String name, String email, int type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newSupportRequest(name, email, type);
                
                JsonResponse response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                supportRequestUUIDString[0] = supportRequest.getUUID().toString();
            }
        });
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewSupportRequest(String name, String email, String type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newSupportRequest(name, email, type);
                
                JsonResponse response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                supportRequestUUIDString[0] = supportRequest.getUUID().toString();
            }
        });
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewAgent(String rainbowId, @SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] agentUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                newAgent(rainbowId, skills);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                agentUUIDString[0] = agent.getUUID().toString();
            }
        });
        
        assumeNotNull(agentUUIDString[0]);
        
        return agentUUIDString[0];
    }
    
    protected static final void customerWaitsForAgent(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                checkSupportRequest(supportRequestUUIDString);
                
                JsonResponse response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assumeFalse("support request already has assigned agent", supportRequest.hasAssignedAgent());
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request already waiting", supportRequest.isWaiting());
            }
        });
        
        executeInNewClient(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                waitForAgent(supportRequestUUIDString);
                
                awaitResponse();
            }
        });
    }
    
    protected static final void agentGetsActivated(String agentUUIDString, Boolean isActivated)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                activateAgentWithUUID(agentUUIDString, isActivated);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue(isActivated.equals(agent.isActivated()));
            }
        });
    }
    
    protected static final void agentUpdatesAvailability(String agentUUIDString, Boolean isAvailable)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                updateAgentAvailabilityWithUUID(agentUUIDString, isAvailable);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue(isAvailable.equals(agent.isAvailable()));
            }
        });
    }
    
    protected static final void agentTakesSupportRequest(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                checkAgentWithUUID(agentUUIDString);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse("agent already has assigned support request", agent.hasAssignedSupportRequest());
                
                assumeTrue("agent not activated", agent.isActivated());
                
                assumeFalse("agent already waiting", agent.isWaiting());
                
                assumeTrue("agent not available", agent.isAvailable());
            }
        });
        
        executeInNewClient(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                takeSupportRequestWithUUID(agentUUIDString);
                
                awaitResponse();
            }
        });
    }
    
    protected static final boolean agentDidTakeSupportRequest(String agentUUIDString, String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        boolean[] didTake = new boolean[] {false};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                checkAgentWithUUID(agentUUIDString);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue("agent not activated", agent.isActivated());
                
                assumeFalse("agent is waiting", agent.isWaiting());
                
                assumeFalse("agent has not been assigned a support request", agent.isAvailable());
                
                assumeTrue("agent is not available", agent.hasAssignedSupportRequest());
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request is waiting", supportRequest.isWaiting());
                
                assumeTrue("support request has not been assigned an agent", supportRequest.hasAssignedAgent());
                
                String assignedSupportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                
                didTake[0] = (supportRequestUUIDString.equals(assignedSupportRequestUUIDString));
            }
        });
        
        return didTake[0];
    }
    
    protected static final void supportRequestGetsRemoved(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeSupportRequest(supportRequestUUIDString);
                
                JsonResponse response = awaitResponse();
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assumeFalse("support request is not closed", supportRequest.isOpen());
                
                assumeFalse("support request is still waiting", supportRequest.isWaiting());
                
                assumeFalse("support request is still assigned an agent", supportRequest.hasAssignedAgent());
                
                checkSupportRequest(supportRequestUUIDString);
                
                response = awaitResponse();
                
                assumeResponseDidNotSucceed(response);
                
                assumeResponseHasErrorPayload(response, "uuid not found");
                
            }
        });
    }
    
    protected static final void agentGetsRemoved(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeAgentWithUUID(agentUUIDString);
                
                JsonResponse response = awaitResponse();
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse("agent is still activated", agent.isActivated());
                
                assumeFalse("agent is still waiting", agent.isWaiting());
                
                assumeFalse("agent is still assigned a support request", agent.isAvailable());
                
                assumeFalse("agent is still available", agent.hasAssignedSupportRequest());
                
                checkAgentWithUUID(agentUUIDString);
                
                response = awaitResponse();
                
                assumeResponseDidNotSucceed(response);
                
                assumeResponseHasErrorPayload(response, "uuid not found");
            }
        });
    }
}
