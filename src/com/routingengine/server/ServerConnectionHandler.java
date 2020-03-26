package com.routingengine.server;

import java.io.IOException;
import java.net.Socket;
import com.routingengine.MethodManager;
import com.routingengine.RoutingEngine;
import com.routingengine.client.ConnectionHandler;
import com.routingengine.json.JsonProtocolException;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


public final class ServerConnectionHandler extends ConnectionHandler
{
    private final MethodManager methodManager;
    
    ServerConnectionHandler(Socket socket, RoutingEngine routingEngine)
        throws IOException
    {
        connect(socket);
        
        methodManager = new MethodManager(routingEngine);
    }
    
    @Override
    protected final void runMainLoop()
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
}
