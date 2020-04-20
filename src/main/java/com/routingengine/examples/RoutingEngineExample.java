package com.routingengine.examples;

import static com.routingengine.Logger.log;
import static com.routingengine.SupportRequest.Type;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.routingengine.client.Client;
import com.routingengine.server.Server;


public class RoutingEngineExample
{
    public static void main(String[] args)
        throws IOException
    {
        log("Start of Routing Engine Test");
        
        final int DEFAULT_NUM_AGENTS = 1;
        
        final int DEFAULT_NUM_CUSTOMERS = 1;
        
        String hostname;
        
        int port;
        
        int numberOfAgents;
        
        int numberOfCustomers;
        
        switch (args.length) {
            case 1:
                hostname = "localhost";
                
                port = Integer.valueOf(args[0]);
                
                numberOfAgents = DEFAULT_NUM_AGENTS;
                
                numberOfCustomers = DEFAULT_NUM_CUSTOMERS;
                
                break;
            
            case 2:
                hostname = args[0];
                
                port = Integer.valueOf(args[1]);
                
                numberOfAgents = DEFAULT_NUM_AGENTS;
                
                numberOfCustomers = DEFAULT_NUM_CUSTOMERS;
                
                break;
            
            case 4:
                hostname = args[0];
                
                port = Integer.valueOf(args[1]);
                
                numberOfAgents = Integer.valueOf(args[2]);
                
                numberOfCustomers = Integer.valueOf(args[3]);
                
                break;
            
            default:
                System.out.println("Usage: java com.routingengine.RoutingEngine [hostname] port [num_agents, num_customers]");
                
                return;
        }
        
        Type[] types = Type.values();
        
        int k = types.length;
        
        int[] numberOfSupportRequestsToService = new int[k];
        
        for (int i = 0; i < k; i++) {
            int numberOfThisCustomer = (numberOfCustomers/k) + (numberOfCustomers%k > i%k ? 1 : 0);
            
            int numberOfThisAgent = (numberOfAgents/k) + (numberOfAgents%k > i%k ? 1 : 0);
            
            if (numberOfThisCustomer > 0 && numberOfThisAgent == 0) {
                log("Not enough of agent: " + Type.of(i));
                
                return;
            }
            
            numberOfSupportRequestsToService[i] = (int) Math.ceil((float) numberOfThisCustomer/numberOfThisAgent);
            
            log("Type: " + types[i] +
                ", Agents: " + numberOfThisAgent +
                ", Customers: " + numberOfThisCustomer +
                ", Support Requests per Agent: " + numberOfSupportRequestsToService[i]);
        }
        
        Server server = new Server(hostname, port);
        
        Thread serverThread = new Thread(server);
        
        log("Starting server...");
        
        serverThread.start();
        
        try {
            TimeUnit.SECONDS.sleep(2);
        }
        
        catch (InterruptedException exception) { }
        
        ExecutorService executorService = newFixedThreadPool(numberOfAgents + numberOfCustomers);
        
        log("Starting clients...");
        
        for (int i = 0; i < numberOfAgents; i++) {
            AgentClientConnectionHandler connectionHandler = new AgentClientConnectionHandler();
            
            connectionHandler.id = i + 1;
            
            connectionHandler.type = i % k;
            
            connectionHandler.numberOfSupportRequestsToService = numberOfSupportRequestsToService[i % k];
            
            Client client = new Client(hostname, port);
            
            client.setConnectionHandler(connectionHandler);
            
            executorService.execute(client);
        }
        
        for (int i = 0; i < numberOfCustomers; i++) {
            CustomerClientConnectionHandler connectionHandler = new CustomerClientConnectionHandler();
            
            connectionHandler.id = i + 1;
            
            connectionHandler.type = i % k;
            
            Client client = new Client(hostname, port);
            
            client.setConnectionHandler(connectionHandler);
            
            executorService.execute(client);
        }
        
        executorService.shutdown();
        
        while (!executorService.isTerminated()) {
            log("Waiting for clients to shut down...");
            
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            }
            
            catch (InterruptedException exception) {
                executorService.shutdownNow();
            }
        }
        
        log("Clients shut down successfully");
        
        log("Interrupting server");
        
        serverThread.interrupt();
        
        try {
            serverThread.join();
        }
        
        catch (InterruptedException exception) {
            log("Interrupted while joining server thread");
        }
        
        log("End of Routing Engine Test");
    }
}
