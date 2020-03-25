package com.routingengine.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.routingengine.Logger;
import com.routingengine.RoutingEngine;


public final class Server
  implements Runnable, Closeable
{
  private final InetSocketAddress address;
  private final RoutingEngine routingEngine;
  private final ExecutorService executorService;
  private final static int THREAD_POOL_SIZE = 1000;
  private final static int SO_TIMEOUT = 1000;
  
  
  public Server(String hostname, int port)
  {
    address = new InetSocketAddress(hostname, port);
    
    routingEngine = new RoutingEngine();
    
    executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  }
  
  @Override
  public final void run()
  {
    try (ServerSocket listener = new ServerSocket()) {
      listener.bind(address);
      Logger.log("Server bound to " + address);
      
      listener.setSoTimeout(SO_TIMEOUT);
      
      while (!Thread.interrupted()) {
        try {
          Socket socket = listener.accept();
          
          ServerConnectionHandler connectionHandler =
              new ServerConnectionHandler(socket, routingEngine);
          
          executorService.execute(connectionHandler);
        }
        
        catch (SocketTimeoutException exception) {
          Logger.log(".");
        }
        
        catch (InterruptedIOException exception) {
          Logger.log("Server interrupted");
          
          break;
        }
      }
    }
    
    catch (IOException exception) {
      // server crashed! what happened?
      
      exception.printStackTrace();
    }
    
    finally {
      close();
    }
  }
  
  @Override
  public final void close()
  {
    if (executorService.isTerminated()) {
      Logger.log("Server has already been closed");
      
      return;
    }
    
    Logger.log("Shutting down executor service");
    executorService.shutdown();
    
    while (!executorService.isTerminated()) {
      try {
        Logger.log("Waiting for executor service to terminate");
        executorService.awaitTermination(10, TimeUnit.SECONDS);
      }
      
      catch (InterruptedException exception) {
        Logger.log("Executor service interrupted while terminating");
      }
    
      finally {
        Logger.log("Shutting down now");
        executorService.shutdownNow();
      }
    }
    
    Logger.log("Server closed successfully");
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
        System.out.println("Usage: java com.routingengine.server.Server [hostname] port");
        return;
    }
    
    try (Server server = new Server(hostname, port)) {
      server.run();
    }
  }
}