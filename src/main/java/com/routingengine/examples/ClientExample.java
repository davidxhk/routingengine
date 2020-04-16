package com.routingengine.examples;

import static com.routingengine.Logger.log;
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
                    
                    JsonResponse response;
                    
                    response = ping();
                    log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", 1);
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", 1, "127.0.0.1");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY", "127.0.0.1");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = checkSupportRequest(supportRequestUUIDs.get(0));
                    log(response.toString());
                    
                    response = changeSupportRequestType(supportRequestUUIDs.get(0), 2);
                    log(response.toString());
                    
                    response = changeSupportRequestType(supportRequestUUIDs.get(1), "CHECK_BILL");
                    log(response.toString());
                    
                    response = closeSupportRequest(supportRequestUUIDs.get(2));
                    log(response.toString());
                    
                    response = removeSupportRequest(supportRequestUUIDs.get(3));
                    log(response.toString());
                    
                    response = newAgent(Map.of(2, true));
                    agentUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = newAgent(Map.of("CHECK_SUBSCRIPTION", true), "127.0.0.1");
                    agentUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    log(response.toString());
                    
                    response = checkAgent(agentUUIDs.get(0));
                    log(response.toString());
                    
                    response = updateAgentSkills(agentUUIDs.get(0), Map.of(1, true));
                    log(response.toString());
                    
                    response = updateAgentSkills(agentUUIDs.get(1), Map.of("GENERAL_ENQUIRY", true));
                    log(response.toString());
                    
                    response = updateAgentAvailability(agentUUIDs.get(0), true);
                    log(response.toString());
                    
                    response = updateAgentAvailability(agentUUIDs.get(1), true);
                    log(response.toString());
                    
//                  response = waitForAgent(supportRequestUUIDs.get(0));
//                  log(response.toString());
                    
//                  response = waitForAgent(supportRequestUUIDs.get(1));
//                  log(response.toString());
                    
//                  response = takeSupportRequest(agentUUIDs.get(0));
//                  log(response.toString());
                    
//                  response = takeSupportRequest(agentUUIDs.get(1));
//                  log(response.toString());
                    
//                  response = dropSupportRequest(agentUUIDs.get(0));
//                  log(response.toString());
                    
//                  response = dropSupportRequest(agentUUIDs.get(0));
//                  log(response.toString());
                    
                    response = removeAgent(agentUUIDs.get(0));
                    log(response.toString());
                    
                    response = getStatusOverview();
                    log(response.toString());
                    
                    response = getAgentStatus();
                    log(response.toString());
                    
                    response = getSupportRequestStatus();
                    log(response.toString());
                    
                    response = getQueueStatus();
                    log(response.toString());
                    
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
