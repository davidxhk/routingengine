package com.routingengine;

import static com.routingengine.json.JsonUtils.*;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.Assume.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import com.google.gson.JsonObject;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;
import com.routingengine.server.Server;


public abstract class MethodTestBase
{
    protected static final String hostname = "localhost";
    protected static final int port = 50000;
    protected static String method;
    protected static Client customer, agent;
    protected static Thread serverThread;
    protected static ExecutorService executor;
    private static final int THREAD_POOL_SIZE = 100;
    
    @BeforeAll
    protected static final void setUpBeforeClass()
        throws Exception
    {
        serverThread = new Thread(new Server(hostname, port));
        serverThread.start();
        
        TimeUnit.SECONDS.sleep(2);
        
        executor = newFixedThreadPool(THREAD_POOL_SIZE);
        
        customer = new Client(hostname, port); 
        agent = new Client(hostname, port);
    }
    
    @AfterAll
    protected static final void tearDownAfterClass()
        throws Exception
    {
        customerExits();
        agentExits();
        
        executor.shutdownNow();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        serverThread.interrupt();
        serverThread.join();
    }
    
    protected static final String generateNewSupportRequest(String name, String email, int type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                supportRequestUUIDString[0] = getAsString(payload, "uuid");
            }
        });
        
        executor.submit(customer).get();
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewSupportRequest(String name, String email, String type)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] supportRequestUUIDString = new String[] {null};
        
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newSupportRequest(name, email, type);
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                supportRequestUUIDString[0] = getAsString(payload, "uuid");
            }
        });
        
        executor.submit(customer).get();
        
        assumeNotNull(supportRequestUUIDString[0]);
        
        return supportRequestUUIDString[0];
    }
    
    protected static final String generateNewAgent(@SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException, ExecutionException
    {
        String[] agentUUIDString = new String[] {null};
        
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = newAgent(skills);
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                agentUUIDString[0] = getAsString(payload, "uuid");
            }
        });
        
        executor.submit(agent).get();
        
        assumeNotNull(agentUUIDString[0]);
        
        return agentUUIDString[0];
    }
    
    protected static final void customerWaitsForAgent(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        final boolean[] isOk = new boolean[] {false};
        
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkSupportRequest(supportRequestUUIDString);
                
                if (!response.didSucceed())
                    return;
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                if (payload == null)
                    return;
                
                SupportRequest supportRequest = SupportRequest.fromJson(payload);
                
                if (!supportRequest.isOpen() || supportRequest.isWaiting() || supportRequest.hasAssignedAgent())
                    return;
                
                isOk[0] = true;
            }
        });
        
        executor.submit(customer).get();
        
        assumeTrue(isOk[0]);
        
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                waitForAgent(supportRequestUUIDString);
            }
        });
        
        executor.execute(customer);
    }
    
    protected static final void agentUpdatesAvailability(String agentUUIDString, Boolean isAvailable)
        throws IOException, InterruptedException, ExecutionException
    {
        final boolean[] didSucceed = new boolean[] {false};
        
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = updateAgentAvailability(agentUUIDString, isAvailable);
                
                if (!response.didSucceed())
                    return;
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                if (payload == null)
                    return;
                
                Agent agent = Agent.fromJson(payload);
                
                if (!isAvailable.equals(agent.isAvailable()))
                    return;
                
                didSucceed[0] = true;
            }
        });
        
        executor.submit(agent).get();
        
        assumeTrue(didSucceed[0]);
    }
    
    protected static final void agentTakesSupportRequest(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        final boolean[] isOk = new boolean[] {false};
        
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                JsonResponse response = checkAgent(agentUUIDString);
                
                if (!response.didSucceed())
                    return;
                
                JsonObject payload = castToJsonObject(response.getPayload());
                
                if (payload == null)
                    return;
                
                Agent agent = Agent.fromJson(payload);
                
                if (!agent.isAvailable())
                    return;
                
                isOk[0] = true;
            }
        });
        
        executor.submit(agent).get();
        
        assumeTrue(isOk[0]);
        
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                takeSupportRequest(agentUUIDString);
            }
        });
        
        executor.execute(agent);
    }
    
    protected static final void removeSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeSupportRequest(supportRequestUUIDString);
            }
        });
        
        executor.submit(customer).get();
    }
    
    protected static final void removeAgent(String agentUUIDString)
        throws IOException, InterruptedException, ExecutionException
    {
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException
            {
                removeAgent(agentUUIDString);
            }
        });
        
        executor.submit(agent).get();
    }
    
    private static final void customerExits()
        throws IOException, InterruptedException, ExecutionException
    {
        customer.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                exit();
            }
        });
        
        executor.submit(customer).get();
    }
    
    private static final void agentExits()
        throws IOException, InterruptedException, ExecutionException
    {
        agent.setConnectionHandler(new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                exit();
            }
        });
        
        executor.submit(agent).get();
    }
}
