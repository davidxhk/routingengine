package com.routingengine.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.routingengine.client.Client;
import com.routingengine.client.ClientConnectionHandler;
import com.routingengine.json.JsonResponse;


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
            
            client.setConnectionHandler(new ClientConnectionHandler()
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
                    supportRequestUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
                    newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY");
                    supportRequestUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
                    newSupportRequest("bob", "bob@gmail.com", 1, "127.0.0.1");
                    supportRequestUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
                    newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY", "127.0.0.1");
                    supportRequestUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
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
                    
                    newAgent(Map.of(2, true));
                    agentUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
                    newAgent(Map.of("CHECK_SUBSCRIPTION", true), "127.0.0.1");
                    agentUUIDs.add(getUUIDFromResponse(awaitResponse()));
                    
                    checkAgent(agentUUIDs.get(0));
                    awaitResponse();
                    
                    updateAgentSkills(agentUUIDs.get(0), Map.of(1, true));
                    awaitResponse();
                    
                    updateAgentSkills(agentUUIDs.get(1), Map.of("GENERAL_ENQUIRY", true));
                    awaitResponse();
                    
                    updateAgentAvailability(agentUUIDs.get(0), true);
                    awaitResponse();
                    
                    updateAgentAvailability(agentUUIDs.get(1), true);
                    awaitResponse();
                    
                    waitForAgent(supportRequestUUIDs.get(0));
                    nextJsonResponse();
                    
                    waitForAgent(supportRequestUUIDs.get(1));
                    nextJsonResponse();
                    
                    takeSupportRequest(agentUUIDs.get(0));
                    nextJsonResponse();
                    
                    takeSupportRequest(agentUUIDs.get(1));
                    nextJsonResponse();
                    
                    dropSupportRequest(agentUUIDs.get(0));
                    awaitResponse();
                    
                    dropSupportRequest(agentUUIDs.get(0));
                    awaitResponse();
                    
                    removeAgent(agentUUIDs.get(0));
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
                
                @Override
                protected JsonResponse awaitResponse()
                    throws IOException, InterruptedException
                {
                    JsonResponse response = super.awaitResponse();
                    
                    log(response.toString());
                    
                    return response;
                }
                
                @Override
                protected JsonResponse nextJsonResponse()
                    throws IOException, InterruptedException
                {
                    JsonResponse response = super.nextJsonResponse();
                    
                    log(response.toString());
                    
                    return response;
                }
                
                private final String getUUIDFromResponse(JsonResponse response)
                {
                    return response.getPayload().getAsJsonObject().get("uuid").getAsString();
                }
            });
            
            client.run();
        }
        
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
