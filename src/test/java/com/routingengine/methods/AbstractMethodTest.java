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
                throws IOException, InterruptedException, EndConnectionException
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
                throws IOException, InterruptedException, EndConnectionException
            {
                connectionHandler.connect(this.socket);
                
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
        if (!response.didSucceed())
            assumeTrue("assumed " + response.getMethod() + " would succeed, instead got " + response.getPayload(), false);
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
    
    protected static final void assertResponseDidSucceed(JsonResponse response)
    {
        if (!response.didSucceed())
            fail("expected " + response.getMethod() + " to fail, instead got " + response.getPayload());
    }
    
    protected static final Agent assertResponseHasAgentPayload(JsonResponse response)
    {
        assumeResponseDidSucceed(response);
        
        Agent agent = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
                
            agent = Agent.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            fail(exception);
        }
        
        assumeNotNull(agent);
        
        return agent;
    }
    
    protected static final SupportRequest assertResponseHasSupportRequestPayload(JsonResponse response)
    {
        assumeResponseDidSucceed(response);
        
        SupportRequest supportRequest = null;
        
        try {
            JsonObject payload = castToJsonObject(response.getPayload());
                
            supportRequest = SupportRequest.fromJson(payload);
        }
        
        catch (IllegalArgumentException exception) {
            fail(exception);
        }
        
        assumeNotNull(supportRequest);
        
        return supportRequest;
    }
    
    protected static final void assertResponseDidNotSucceed(JsonResponse response)
    {
        if (response.didSucceed())
            fail("expected " + response.getMethod() + " to fail, instead got " + response.getPayload());
    }
    
    protected static final void assertResponseHasErrorPayload(JsonResponse response, String error)
    {
        String payload = castToString(response.getPayload());
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                supportRequestUUIDString[0] = supportRequest.getUUID().toString();
            }
        });
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewAgent(@SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] agentUUIDString = new String[] {null};
        
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = newAgent(skills);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = checkSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                assumeFalse("support request already has assigned agent", supportRequest.hasAssignedAgent());
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request already waiting", supportRequest.isWaiting());
            }
        });
        
        executeInNewClient(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                waitForAgent(supportRequestUUIDString);
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = activateAgent(agentUUIDString, isActivated);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = updateAgentAvailability(agentUUIDString, isAvailable);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
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
                throws IOException, InterruptedException, EndConnectionException
            {
                takeSupportRequest(agentUUIDString);
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
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = Agent.fromJson(castToJsonObject(response.getPayload()));
                
                assumeTrue("agent not activated", agent.isActivated());
                
                assumeFalse("agent is waiting", agent.isWaiting());
                
                assumeFalse("agent has not been assigned a support request", agent.isAvailable());
                
                assumeTrue("agent is not available", agent.hasAssignedSupportRequest());
                
                response = checkSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = SupportRequest.fromJson(castToJsonObject(response.getPayload()));
                
                assumeTrue("support request is closed", supportRequest.isOpen());
                
                assumeFalse("support request is waiting", supportRequest.isWaiting());
                
                assumeTrue("support request has not been assigned an agent", supportRequest.hasAssignedAgent());
                
                String assignedSupportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                
                didTake[0] = (supportRequestUUIDString.equals(assignedSupportRequestUUIDString));
            }
        });
        
        return didTake[0];
    }
    
    protected static final void removeSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = removeSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
            }
        });
    }
    
    protected static final void removeAgent(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        execute(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                JsonResponse response = removeAgent(agentUUIDString);
                
                assertResponseDidSucceed(response);
            }
        });
    }
}
