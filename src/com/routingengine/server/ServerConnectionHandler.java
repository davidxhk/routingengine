package com.routingengine.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import com.routingengine.Logger;
import com.routingengine.MethodManager;
import com.routingengine.RoutingEngine;
import com.routingengine.client.ConnectionHandler;
import com.routingengine.json.JsonProtocolException;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public final class ServerConnectionHandler extends ConnectionHandler
    implements Runnable, Closeable
{
    private final MethodManager methodManager;
    
    ServerConnectionHandler(Socket socket, RoutingEngine routingEngine)
        throws IOException
    {
        connect(socket);
        
        Logger.log("Server connected to " + socket.toString());
        
        methodManager = new MethodManager(routingEngine);
    }
    
    @Override
    public final void runMainLoop()
        throws IOException, InterruptedException
    {
        while (socket.isConnected() && !Thread.interrupted()) {
            try {
                waitForInput();
            }
            
            catch (InterruptedException exception) {
                JsonResponse
                    .failure("server connection handler interrupted while waiting for input")
                    .writeSafe(jsonWriter);
                
                Thread.currentThread().interrupt();
                
                throw exception;
            }
            
            JsonRequest jsonRequest = new JsonRequest();
            
            try {
                jsonRequest.read(jsonReader);
            }
            
            catch (JsonProtocolException exception) {
                JsonResponse
                    .failure(exception.getMessage())
                    .writeSafe(jsonWriter);
                
                continue;
            }
            
            if (jsonRequest.getMethod().matches("new_agent|new_support_request")) {
                if (!jsonRequest.hasArgument("address"))
                    jsonRequest.setArgument("address", getAddress());
            }
            
            JsonResponse jsonResponse = new JsonResponse();
            
            jsonRequest.service(methodManager, jsonResponse);
            
            jsonResponse.writeSafe(jsonWriter);
        }
    }

    @Override
    public final void run()
    {
        try {            
            runMainLoop();
        }
        
        catch (IOException exception) {
            Logger.log("I/O error in " + socket.toString());
            
            exception.printStackTrace();    
        }
        
        catch (InterruptedException exception) {
            Logger.log("Server connection was interrupted");
        }
        
        finally {
            close();
        }
    }
    
    @Override
    public final void close()
    {
        try {
            socket.close();
        }
        
        catch (IOException e) {
            Logger.log("Failed to close " + socket.toString());
        }
        
        Logger.log("Server connection closed");
    }
}
