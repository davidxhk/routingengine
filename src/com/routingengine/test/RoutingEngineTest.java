package com.routingengine.test;

import static com.routingengine.Logger.log;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.routingengine.client.Client;
import com.routingengine.server.Server;


public class RoutingEngineTest
{
    public static void main(String[] args)
        throws IOException
    {
        log("Start of Routing Engine Test");
        
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
                System.out.println("Usage: java com.routingengine.test.RoutingEngineTest [hostname] port");
                return;
        }
        
        Random random = new Random(System.currentTimeMillis());
        
        Server server = new Server(hostname, port);
        Thread serverThread = new Thread(server);
        serverThread.start();
        
        final int NUM_AGENTS = 3;
        final int NUM_CUSTOMERS = 21;
        
        ExecutorService executorService = newFixedThreadPool(NUM_AGENTS + NUM_CUSTOMERS);
        
        for (int i = 0; i < NUM_AGENTS; i++) {
            
            AgentClientConnectionHandler connectionHandler = new AgentClientConnectionHandler();
            connectionHandler.i = i+1;
            connectionHandler.j = NUM_CUSTOMERS/NUM_AGENTS;
            connectionHandler.random = random;
            
            Client client = new Client(hostname, port);
            client.setConnectionHandler(connectionHandler);
            
            executorService.execute(client);
        }
        
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            
            CustomerClientConnectionHandler connectionHandler = new CustomerClientConnectionHandler();
            connectionHandler.i = i+1;
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
        
        Thread.getAllStackTraces().keySet().forEach(t ->
            log(t.getName() + " -> daemon=" + t.isDaemon() + ", alive=" + t.isAlive()));
        
        log("End of Routing Engine Test");
    }
}
