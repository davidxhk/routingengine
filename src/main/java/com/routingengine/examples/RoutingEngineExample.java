package com.routingengine.examples;

import static com.routingengine.Logger.log;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.util.Random;
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
        
        final int DEFAULT_NUM_AGENTS = 20;
        final int DEFAULT_NUM_CUSTOMERS = 99;
        
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
        
        Random random = new Random(System.currentTimeMillis());
        
        Server server = new Server(hostname, port);
        Thread serverThread = new Thread(server);
        serverThread.start();
        
        try {
            TimeUnit.SECONDS.sleep(2);
        }
        
        catch (InterruptedException exception) { }
        
        ExecutorService executorService = newFixedThreadPool(numberOfAgents + numberOfCustomers);
        
        int[] numberOfSupportRequestsToService = new int[3];
        for (int i = 0; i < 3; i++) {
            int numberOfThisRequest = (numberOfCustomers/3) + (numberOfCustomers%3 > i%3 ? 1 : 0);
            int numberOfThisAgent = (numberOfAgents/3) + (numberOfAgents%3 > i%3 ? 1 : 0);
            numberOfSupportRequestsToService[i] = numberOfThisRequest/numberOfThisAgent+1;
        }
        
        for (int i = 0; i < numberOfAgents; i++) {
            AgentClientConnectionHandler connectionHandler = new AgentClientConnectionHandler();
            connectionHandler.id = i + 1;
            connectionHandler.numberOfSupportRequestsToService = numberOfSupportRequestsToService[i%3];
            connectionHandler.random = random;
            
            Client client = new Client(hostname, port);
            client.setConnectionHandler(connectionHandler);
            
            executorService.execute(client);
        }
        
        log("Number of customers: " + numberOfCustomers);
        for (int i = 0; i < numberOfCustomers; i++) {
            CustomerClientConnectionHandler connectionHandler = new CustomerClientConnectionHandler();
            connectionHandler.id = i + 1;
            connectionHandler.random = random;
            
            Client client = new Client(hostname, port);
            client.setConnectionHandler(connectionHandler);
            
            executorService.execute(client);
        }
        
        executorService.shutdown();
        
        while (!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            }
            
            catch (InterruptedException exception) {
                executorService.shutdownNow();
            }
        }
        
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
