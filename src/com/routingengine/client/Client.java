package com.routingengine.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.routingengine.Logger;
import com.routingengine.json.JsonResponse;


public class Client
    implements Runnable, Closeable
{
    protected Socket socket;
    protected ClientConnectionHandler connectionHandler = null;
    
    public Client(String hostname, int port)
        throws IOException
    {
        socket = new Socket(hostname, port);
    }
    
    public final Client setConnectionHandler(ClientConnectionHandler clientConnectionHandler)
        throws IOException
    {
        clientConnectionHandler.connect(socket);
        
        this.connectionHandler = clientConnectionHandler;
        
        return this;
    }
    
    @Override
    public final void run()
    {
        connectionHandler.run();
    }
    
    @Override
    public final void close()
    {
        if (connectionHandler == null)
            ConnectionHandler.closeQuietly(socket);
    }
    
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
        
        try (Client client = new Client(hostname, port)) {
            
            client.setConnectionHandler(new ClientConnectionHandler()
            {
                @Override
                protected final void runMainLoop()
                    throws IOException, InterruptedException
                {
                    List<String> supportRequestUUIDs = new ArrayList<>();
                    List<String> agentUUIDs = new ArrayList<>();
                    
                    JsonResponse response;
                    
                    response = ping();
                    Logger.log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", 1);
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", 1, "127.0.0.1");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = newSupportRequest("bob", "bob@gmail.com", "GENERAL_ENQUIRY", "127.0.0.1");
                    supportRequestUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = checkSupportRequest(supportRequestUUIDs.get(0));
                    Logger.log(response.toString());
                    
                    response = changeSupportRequestType(supportRequestUUIDs.get(0), 2);
                    Logger.log(response.toString());
                    
                    response = changeSupportRequestType(supportRequestUUIDs.get(1), "CHECK_BILL");
                    Logger.log(response.toString());
                    
                    response = closeSupportRequest(supportRequestUUIDs.get(2));
                    Logger.log(response.toString());
                    
                    response = removeSupportRequest(supportRequestUUIDs.get(3));
                    Logger.log(response.toString());
                    
                    response = newAgent(Map.of(2, true));
                    agentUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = newAgent(Map.of("CHECK_SUBSCRIPTION", true), "127.0.0.1");
                    agentUUIDs.add(response.getPayload().getAsJsonObject().get("uuid").getAsString());
                    Logger.log(response.toString());
                    
                    response = checkAgent(agentUUIDs.get(0));
                    Logger.log(response.toString());
                    
                    response = updateAgentSkills(agentUUIDs.get(0), Map.of(1, true));
                    Logger.log(response.toString());
                    
                    response = updateAgentSkills(agentUUIDs.get(1), Map.of("GENERAL_ENQUIRY", true));
                    Logger.log(response.toString());
                    
                    response = updateAgentAvailability(agentUUIDs.get(0), true);
                    Logger.log(response.toString());
                    
                    response = updateAgentAvailability(agentUUIDs.get(1), true);
                    Logger.log(response.toString());
                    
//                  response = waitForAgent(supportRequestUUIDs.get(0));
//                  Logger.log(response.toString());
                    
//                  response = waitForAgent(supportRequestUUIDs.get(1));
//                  Logger.log(response.toString());
                    
//                  response = takeSupportRequest(agentUUIDs.get(0));
//                  Logger.log(response.toString());
                    
//                  response = takeSupportRequest(agentUUIDs.get(1));
//                  Logger.log(response.toString());
                    
//                  response = dropSupportRequest(agentUUIDs.get(0));
//                  Logger.log(response.toString());
                    
//                  response = dropSupportRequest(agentUUIDs.get(0));
//                  Logger.log(response.toString());
                    
                    response = removeAgent(agentUUIDs.get(0));
                    Logger.log(response.toString());
                    
                    response = getStatusOverview();
                    Logger.log(response.toString());
                    
                    response = getAgentStatus();
                    Logger.log(response.toString());
                    
                    response = getSupportRequestStatus();
                    Logger.log(response.toString());
                    
                    response = getQueueStatus();
                    Logger.log(response.toString());
                }
            });
            
            client.run();
        }
        
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
