package com.routingengine.server;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import static com.routingengine.websocket.WebSocketProtocol.doClosingHandshake;
import static com.routingengine.websocket.WebSocketProtocol.WebSocketProtocolException;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final ExecutorService executorService;
    private final List<Future<JsonResponse>> pendingResponses;
    private static final int THREAD_POOL_SIZE = 100;
    private static final int PENDING_TIMEOUT = 50;
    private static final AtomicInteger TICKET_COUNTER = new AtomicInteger(1);
    
    ServerConnectionHandler(Socket socket, RoutingEngine routingEngine)
        throws IOException
    {
        connect(socket);
            
        log("Server connected to " + socket.toString());
        
        methodManager = new MethodManager(routingEngine);
        
        executorService = newFixedThreadPool(THREAD_POOL_SIZE);
        
        pendingResponses = new ArrayList<>();
    }
    
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        WebSocketJsonReader webSocketJsonReader = new WebSocketJsonReader(inputStream);
        
        webSocketJsonReader.setMasked(true);
        
        webSocketJsonReader.initializeReader();
        
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
                boolean inputReceived = false;
                
                while (!inputReceived) {
                    checkPendingResponses();
                    
                    inputReceived = waitForInput();
                }
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
                    .setNullTicketNumber()
                    .writeTo(jsonWriter);
                
                continue;
            }
            
            log("Server got request –> " + jsonRequest.toString());
            
            if (jsonRequest.getMethod().matches("new_agent|new_support_request")) {
                
                if (!jsonRequest.hasArgument("address"))
                    jsonRequest.setArgument("address", getAddress());
            }
            
            int ticketNumber = TICKET_COUNTER.getAndIncrement();
            
            Future<JsonResponse> pendingResponse = executorService.submit(() -> {
                JsonResponse response;
                
                try {
                    JsonElement payload = methodManager.handle(
                        jsonRequest.getMethod(),
                        jsonRequest.getArguments());
                    
                    response = JsonResponse.success(jsonRequest, payload);
                }
                
                catch (IllegalArgumentException | IllegalStateException exception) {
                    response = JsonResponse.failure(jsonRequest, exception);
                }
                
                response.setTicketNumber(ticketNumber);
                
                return response;
            });
            
            try {
                handlePendingResponse(pendingResponse);
            }
            
            catch (TimeoutException exception) {
                pendingResponses.add(pendingResponse);
                
                JsonResponse
                    .pending(jsonRequest)
                    .setTicketNumber(ticketNumber)
                    .writeTo(jsonWriter);
            }
        }
    }
    
    private final void checkPendingResponses()
        throws IOException, InterruptedException
    {
        int i = 0;
        
        while (i < pendingResponses.size()) {
            Future<JsonResponse> pendingResponse = pendingResponses.get(i);
            
            try {
                handlePendingResponse(pendingResponse);
                
                pendingResponses.remove(i);
                
                continue;
            }
            
            catch (TimeoutException exception) { }
            
            i++;
        }
    }
    
    private final void handlePendingResponse(Future<JsonResponse> pendingResponse)
        throws IOException, TimeoutException, InterruptedException
    {
        try {
            JsonResponse response = pendingResponse.get(PENDING_TIMEOUT, TimeUnit.MILLISECONDS);
            
            response.writeTo(jsonWriter);
        }
        
        catch (ExecutionException exception) {
            log("Error while handling method: " + exception.getCause().getMessage());
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
        executorService.shutdownNow();
        
        try {
            socket.close();
        }
        
        catch (IOException e) {
            log("Failed to close " + socket.toString());
        }
        
        log("Server connection closed");
    }
}
