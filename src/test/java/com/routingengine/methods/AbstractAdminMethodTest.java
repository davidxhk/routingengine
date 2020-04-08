package com.routingengine.methods;

import static org.junit.Assume.assumeTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeNotNull;
import static com.routingengine.SupportRequest.Type;
import static com.routingengine.json.JsonConnectionHandler.SLEEP_MILLIS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.routingengine.Agent;
import com.routingengine.SupportRequest;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


public class AbstractAdminMethodTest extends AbstractMethodTest
{
    protected final ArrayList<String> allAgents = new ArrayList<>();
    protected final ArrayList<String> inactiveAgents = new ArrayList<>();
    protected final ArrayList<String> activeAgents = new ArrayList<>();
    protected final ArrayList<String> unavailableAgents = new ArrayList<>();
    protected final ArrayList<String> availableAgents = new ArrayList<>();
    protected final ArrayList<String> waitingAgents = new ArrayList<>();
    protected final ArrayList<String> assignedAgents = new ArrayList<>();
    
    protected final ArrayList<String> allSupportRequests = new ArrayList<>();
    protected final ArrayList<String> closedSupportRequests = new ArrayList<>();
    protected final ArrayList<String> openSupportRequests = new ArrayList<>();
    protected final ArrayList<String> waitingSupportRequests = new ArrayList<>();
    protected final ArrayList<String> assignedSupportRequests = new ArrayList<>();
    
    protected final HashMap<Type, ArrayList<String>> queuedSupportRequests = new HashMap<>();
    
    private final Random random = new Random();
    
    public AbstractAdminMethodTest()
    {
        for (Type type : Type.values())
            queuedSupportRequests.put(type, new ArrayList<>());
    }
    
    protected final void generateNewAgents(int numberOfAgents, @SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException, ExecutionException
    {
        for (int i = 0; i < numberOfAgents; i++) {
            String agentUUIDString = generateNewAgent(skills);
            
            allAgents.add(agentUUIDString);
            
            activeAgents.add(agentUUIDString);
            
            unavailableAgents.add(agentUUIDString);
        }
    }
    
    protected final void agentsUpdateAvailability(int numberOfAgents, boolean isAvailable)
        throws IOException, InterruptedException, ExecutionException
    {
        if (isAvailable) {
            assumeTrue("not enough unavailable agents", numberOfAgents <= unavailableAgents.size());
            
            for (int i = 0; i < numberOfAgents; i++) {
                int agentIndex = unavailableAgents.size() == 1 ? 0 : random.nextInt(unavailableAgents.size() - 1);
                
                String agentUUIDString = unavailableAgents.remove(agentIndex);
                
                agentUpdatesAvailability(agentUUIDString, true);
                
                availableAgents.add(agentUUIDString);
            }
        }
        
        else {
            assumeTrue("not enough available agents", numberOfAgents <= availableAgents.size());
            
            for (int i = 0; i < numberOfAgents; i++) {
                int agentIndex = availableAgents.size() == 1 ? 0 : random.nextInt(availableAgents.size() - 1);
                
                String agentUUIDString = availableAgents.remove(agentIndex);
                
                agentUpdatesAvailability(agentUUIDString, false);
                
                unavailableAgents.add(agentUUIDString);
            }
        }
    }
    
    protected final void agentsTakeSupportRequest(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough available agents", numberOfAgents <= availableAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler checkAgentStatus = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                if (agent.isWaiting())
                    waitingAgents.add(agentUUIDString[0]);
                
                else if (agent.hasAssignedSupportRequest()) {
                    assignedAgents.add(agentUUIDString[0]);
                    
                    SupportRequest supportRequest = agent.getAssignedSupportRequest();
                    
                    String supportRequestUUIDString = supportRequest.getUUID().toString();
                    
                    assumeTrue(waitingSupportRequests.remove(supportRequestUUIDString));
                    
                    assumeTrue(queuedSupportRequests.get(supportRequest.getType())
                        .remove(supportRequestUUIDString));
                    
                    assignedSupportRequests.add(supportRequestUUIDString);
                }
                
                else
                    assumeTrue("agent took support request but not waiting or didn't get assigned a support request", false);
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = availableAgents.size() == 1 ? 0 : random.nextInt(availableAgents.size() - 1);
            
            agentUUIDString[0] = availableAgents.remove(agentIndex);
            
            agentTakesSupportRequest(agentUUIDString[0]);
            
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
            
            execute(checkAgentStatus);
        }
    }
    
    protected final void agentsCloseSupportRequest(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough assigned agents", numberOfAgents <= assignedAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler closeSupportRequest = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue(agent.hasAssignedSupportRequest());
                
                String supportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                
                response = closeSupportRequest(supportRequestUUIDString);
                
                assumeResponseDidSucceed(response);
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assumeFalse(supportRequest.isOpen());
                
                assumeTrue(assignedSupportRequests.remove(supportRequestUUIDString));
                
                closedSupportRequests.add(supportRequestUUIDString);
                
                response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse(agent.hasAssignedSupportRequest());
                
                assumeTrue(agent.isAvailable());
                
                availableAgents.add(agentUUIDString[0]);
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = assignedAgents.size() == 1 ? 0 : random.nextInt(assignedAgents.size() - 1);
            
            agentUUIDString[0] = assignedAgents.remove(agentIndex);
            
            execute(closeSupportRequest);
        }
    }
    
    protected final void agentsDropSupportRequest(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough assigned agents", numberOfAgents <= assignedAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler dropSupportRequest = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue(agent.hasAssignedSupportRequest());
                
                String supportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                
                response = dropSupportRequest(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse(agent.hasAssignedSupportRequest());
                
                assumeTrue(agent.isAvailable());
                
                availableAgents.add(agentUUIDString[0]);
                
                response = checkSupportRequest(supportRequestUUIDString);
                
                SupportRequest supportRequest = assumeResponseHasSupportRequestPayload(response);
                
                assumeFalse(supportRequest.hasAssignedAgent());
                
                assumeTrue(supportRequest.isOpen());
                
                assumeTrue(assignedSupportRequests.remove(supportRequestUUIDString));
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = assignedAgents.size() == 1 ? 0 : random.nextInt(assignedAgents.size() - 1);
            
            agentUUIDString[0] = assignedAgents.remove(agentIndex);
            
            execute(dropSupportRequest);
        }
    }
    
    protected final void activateAgents(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough inactive agents", numberOfAgents <= inactiveAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler activateAgent = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse(agent.isActivated());
                
                response = activateAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                agent = assumeResponseHasAgentPayload(response);
                
                assumeTrue(agent.isActivated());
                
                activeAgents.add(agentUUIDString[0]);
                
                unavailableAgents.add(agentUUIDString[0]);
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = inactiveAgents.size() == 1 ? 0 : random.nextInt(inactiveAgents.size() - 1);
            
            agentUUIDString[0] = inactiveAgents.remove(agentIndex);
            
            execute(activateAgent);
        }
    }
    
    protected final void deactivateAgents(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough active agents", numberOfAgents <= activeAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler deactivateAgent = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                if (agent.hasAssignedSupportRequest()) {
                    String supportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                    
                    assumeTrue(assignedSupportRequests.remove(supportRequestUUIDString));
                    
                    assumeTrue(assignedAgents.remove(agentUUIDString[0]));
                }
                
                else if (agent.isWaiting())
                    assumeTrue(waitingAgents.remove(agentUUIDString[0]));
                
                else if (agent.isAvailable())
                    assumeTrue(availableAgents.remove(agentUUIDString[0]));
                
                else
                    assumeTrue(unavailableAgents.remove(agentUUIDString[0]));
                
                response = deactivateAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse(agent.isActivated());
                
                inactiveAgents.add(agentUUIDString[0]);
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = activeAgents.size() == 1 ? 0 : random.nextInt(activeAgents.size() - 1);
            
            agentUUIDString[0] = activeAgents.remove(agentIndex);
            
            execute(deactivateAgent);
        }
    }
    
    protected final void removeAgents(int numberOfAgents)
        throws IOException, InterruptedException, ExecutionException
    {
        assumeTrue("not enough agents", numberOfAgents <= allAgents.size());
        
        final String agentUUIDString[] = new String[] {null};
        
        ClientConnectionHandler removeAgent = new ClientConnectionHandler()
        {
            @Override
            public void runMainLoop()
                throws IOException, InterruptedException, EndConnectionException
            {
                assumeNotNull(agentUUIDString[0]);
                
                JsonResponse response = checkAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                Agent agent = assumeResponseHasAgentPayload(response);
                
                if (!agent.isActivated())
                    assumeTrue(inactiveAgents.remove(agentUUIDString[0]));
                
                else {
                    activeAgents.remove(agentUUIDString[0]);
                    
                    if (agent.hasAssignedSupportRequest()) {
                        String supportRequestUUIDString = agent.getAssignedSupportRequest().getUUID().toString();
                        
                        assumeTrue(assignedSupportRequests.remove(supportRequestUUIDString));
                        
                        assumeTrue(assignedAgents.remove(agentUUIDString[0]));
                    }
                    
                    else if (agent.isWaiting())
                        assumeTrue(waitingAgents.remove(agentUUIDString[0]));
                    
                    else if (agent.isAvailable())
                        assumeTrue(availableAgents.remove(agentUUIDString[0]));
                    
                    else
                        assumeTrue(unavailableAgents.remove(agentUUIDString[0]));
                }
                
                response = removeAgent(agentUUIDString[0]);
                
                assumeResponseDidSucceed(response);
                
                agent = assumeResponseHasAgentPayload(response);
                
                assumeFalse(agent.isActivated());
            }
        };
        
        for (int i = 0; i < numberOfAgents; i++) {
            int agentIndex = allAgents.size() == 1 ? 0 : random.nextInt(allAgents.size() - 1);
            
            agentUUIDString[0] = allAgents.remove(agentIndex);
            
            execute(removeAgent);
        }
    }
    
    protected final void generateNewSupportRequests(int numberOfSupportRequests, int typeIndex)
        throws IOException, InterruptedException, ExecutionException
    {
        for (int i = 0; i < numberOfSupportRequests; i++) {
            String supportRequestUUIDString = generateNewSupportRequest("bob", "bob@abc.com", typeIndex);
            
            allSupportRequests.add(supportRequestUUIDString);
            
            openSupportRequests.add(supportRequestUUIDString);
        }
    }
    
    
}
