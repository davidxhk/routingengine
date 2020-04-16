package com.routingengine.server;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import static com.routingengine.websocket.WebSocketProtocol.doClosingHandshake;
import static com.routingengine.websocket.WebSocketProtocol.WebSocketProtocolException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.google.gson.JsonElement;
import com.routingengine.MethodManager;
import com.routingengine.RoutingEngine;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;
import com.routingengine.json.JsonWriter;
import com.routingengine.json.JsonProtocol.ExitConnectionException;
import com.routingengine.websocket.WebSocketJsonReader;
import com.routingengine.websocket.WebSocketJsonWriter;


public final class ServerConnectionHandler extends JsonConnectionHandler
    implements Runnable, Closeable
{
    private final MethodManager methodManager;
    
    ServerConnectionHandler(Socket socket, RoutingEngine routingEngine)
        throws IOException
    {
        connect(socket);
            
        log("Server connected to " + socket.toString());
        
        methodManager = new MethodManager(routingEngine);
    }
    
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        WebSocketJsonReader webSocketJsonReader = new WebSocketJsonReader(inputStream);
        
        webSocketJsonReader.setMasked(true);
        
        return (JsonReader) webSocketJsonReader;
    }
    
    @Override
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        WebSocketJsonWriter webSocketJsonWriter = new WebSocketJsonWriter(outputStream);
        
        webSocketJsonWriter.setMasked(false);
        
        return (JsonWriter) webSocketJsonWriter;
    }
    
    @Override
    public final void runMainLoop()
        throws IOException, InterruptedException
    {
        while (!socket.isClosed()) {
            try {
                waitForInput();
            }
            
            catch (InterruptedException exception) {
                JsonResponse
                    .failure("server connection interrupted")
                    .writeTo(jsonWriter);
                
                Thread.currentThread().interrupt();
                
                throw exception;
            }
            
            JsonRequest jsonRequest = new JsonRequest();
            
            try {
                jsonRequest.readFrom(jsonReader);
            }
            
            catch (JsonProtocolException exception) {
                log("Server got bad request");
                
                JsonResponse
                    .failure(jsonRequest, exception)
                    .writeTo(jsonWriter);
                
                continue;
            }
            
            log("Server got request –> " + jsonRequest.toString());
            
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
                    .writeTo(jsonWriter);
            }
            
            catch (IllegalArgumentException | IllegalStateException exception) {
                JsonResponse
                    .failure(jsonRequest, exception)
                    .writeTo(jsonWriter);
            }
        }
    }
    
    @Override
    public final void run()
    {
        try {
            try {
                doClosingHandshake(socket);
            }
            
            catch (WebSocketProtocolException exception) {
                log("WebSocket exception: " + exception.getMessage());
                
                return;
            }
            
            runMainLoop();
        }
        
        catch (InterruptedException exception) {
            log("Server connection was interrupted");
        }
        
        catch (ExitConnectionException exception) {
            log("Server connection was exited");
        }
        
        catch (IOException exception) {
            log("I/O exception: " + exception.getMessage());
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
