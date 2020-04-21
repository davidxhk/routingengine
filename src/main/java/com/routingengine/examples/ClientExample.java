package com.routingengine.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.routingengine.client.Client;
import com.routingengine.client.LogClientConnectionHandler;


public class ClientExample
{
    public static void main(String[] args)
    {
        String hostname;
        int port;
        
        switch (args.length) {
            case 1:
                hostname = "localhost";
                port = Integer.valueOf(args[0]);
                break;
        
            case 2:
                hostname = args[0];
                port = Integer.valueOf(args[1]);
                break;
        
            default:
                System.out.println("Usage: java com.routingengine.client.Client [hostname] port");
                return;
        }
        
        try {
            
            Client client = new Client(hostname, port);
            
            client.setConnectionHandler(new LogClientConnectionHandler()
            {
                @Override
                public final void runMainLoop()
                    throws IOException, InterruptedException
                {
                    List<String> supportRequestUUIDs = new ArrayList<>();
                    List<String> agentUUIDs = new ArrayList<>();
                    
                    ping();
                    awaitResponse();
                    
                    newSupportRequest("bob", "bob@gmail.com", 1);
                    supportRequestUUIDs.add(getSupportRequest(awaitResponse()).getUUID().toString());
                    
                    newSupportRequest("amy", "amy@gmail.com", "GENERAL_ENQUIRY");
                    supportRequestUUIDs.add(getSupportRequest(awaitResponse()).getUUID().toString());
                    
                    newSupportRequest("tom", "tom@gmail.com", 0);
                    supportRequestUUIDs.add(getSupportRequest(awaitResponse()).getUUID().toString());
                    
                    newSupportRequest("kat", "kat@gmail.com", "CHECK_SUBSCRIPTION");
                    supportRequestUUIDs.add(getSupportRequest(awaitResponse()).getUUID().toString());
                    
                    checkSupportRequest(supportRequestUUIDs.get(0));
                    awaitResponse();
                    
                    changeSupportRequestType(supportRequestUUIDs.get(0), 2);
                    awaitResponse();
                    
                    changeSupportRequestType(supportRequestUUIDs.get(1), "CHECK_BILL");
                    awaitResponse();
                    
                    closeSupportRequest(supportRequestUUIDs.get(2));
                    awaitResponse();
                    
                    removeSupportRequest(supportRequestUUIDs.get(3));
                    awaitResponse();
                    
                    newAgent("rainbow_agent_1", Map.of(2, true));
                    agentUUIDs.add(getAgent(awaitResponse()).getUUID().toString());
                    
                    newAgent("rainbow_agent_2", Map.of("CHECK_SUBSCRIPTION", true));
                    agentUUIDs.add(getAgent(awaitResponse()).getUUID().toString());
                    
                    checkAgentWithUUID(agentUUIDs.get(0));
                    awaitResponse();
                    
                    checkAgentWithRainbowId("rainbow_agent_2");
                    awaitResponse();
                    
                    updateAgentSkillsWithUUID(agentUUIDs.get(0), Map.of(1, true));
                    awaitResponse();
                    
                    updateAgentSkillsWithRainbowId("rainbow_agent_2", Map.of("GENERAL_ENQUIRY", true));
                    awaitResponse();
                    
                    updateAgentAvailabilityWithUUID(agentUUIDs.get(0), true);
                    awaitResponse();
                    
                    updateAgentAvailabilityWithRainbowId(agentUUIDs.get(1), true);
                    awaitResponse();
                    
                    waitForAgent(supportRequestUUIDs.get(0));
                    nextJsonResponse();
                    
                    waitForAgent(supportRequestUUIDs.get(1));
                    nextJsonResponse();
                    
                    takeSupportRequestWithUUID(agentUUIDs.get(0));
                    nextJsonResponse();
                    
                    takeSupportRequestWithRainbowId("rainbow_agent_2");
                    nextJsonResponse();
                    
                    dropSupportRequestWithUUID(agentUUIDs.get(0));
                    awaitResponse();
                    
                    dropSupportRequestWithRainbowId("rainbow_agent_2");
                    awaitResponse();
                    
                    removeAgentWithUUID(agentUUIDs.get(0));
                    awaitResponse();
                    
                    removeAgentWithRainbowId("rainbow_agent_2");
                    awaitResponse();
                    
                    newAdmin();
                    awaitResponse();
                    
                    newAdmin(agentUUIDs.get(1));
                    awaitResponse();
                    
                    getStatusOverview();
                    awaitResponse();
                    
                    getAgentStatus();
                    awaitResponse();
                    
                    getSupportRequestStatus();
                    awaitResponse();
                    
                    getQueueStatus();
                    awaitResponse();
                    
                    exit();
                }
            });
            
            client.run();
        }
        
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
