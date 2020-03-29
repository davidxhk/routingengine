package com.routingengine.server;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.JsonElement;
import com.routingengine.MethodManager;
import com.routingengine.RoutingEngine;
import com.routingengine.client.ConnectionHandler;
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
        
        // log("Server connected to " + socket.toString());
        
        methodManager = new MethodManager(routingEngine);
    }
    
    @Override
    public final void runMainLoop()
        throws IOException, InterruptedException, EndConnectionException
    {
        while (socket.isConnected()) {
            try {
                waitForInput();
            }
            
            catch (InterruptedException exception) {
                JsonResponse
                    .failure("server connection interrupted")
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
                    .failure(jsonRequest, exception)
                    .writeSafe(jsonWriter);
                
                continue;
            }
            
            catch (EndConnectionException exception) {
                jsonWriter.writeLine("Goodbye!");
                jsonWriter.flush();
                
                throw exception;
            }
            
            if (jsonRequest.getMethod().matches("new_agent|new_support_request")) {
                if (!jsonRequest.hasArgument("address"))
                    jsonRequest.setArgument("address", getAddress());
            }
            
            try {
                JsonElement payload = methodManager.handle(
                    jsonRequest.getMethod(),
                    jsonRequest.getArguments());
                
                JsonResponse
                    .success(jsonRequest, payload)
                    .writeSafe(jsonWriter);
            }
            
            catch (IllegalArgumentException | IllegalStateException exception) {
                JsonResponse
                    .failure(jsonRequest, exception)
                    .writeSafe(jsonWriter);
            }
        }
    }

    @Override
    public final void run()
    {
        //log("Server handling " + socket.toString());
        
        try {
            runMainLoop();
        }
        
        catch (IOException exception) {
            log("I/O error in " + socket.toString());
            
            exception.printStackTrace();    
        }
        
        catch (InterruptedException exception) {
            log("Server connection was interrupted");
        }
        
        catch (EndConnectionException exception) {
            log("Server connection was exited");
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
            log("Failed to close " + socket.toString());
        }
        
        log("Server connection closed");
    }
}
