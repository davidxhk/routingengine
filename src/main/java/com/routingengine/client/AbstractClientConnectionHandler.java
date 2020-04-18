package com.routingengine.client;

import static com.routingengine.Logger.log;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonResponse;
import com.routingengine.json.JsonWriter;
import com.routingengine.json.JsonProtocol.JsonProtocolException;
import com.routingengine.websocket.WebSocketJsonReader;
import com.routingengine.websocket.WebSocketJsonWriter;


public abstract class AbstractClientConnectionHandler extends JsonConnectionHandler
{
    private static final int RESPONSE_BUFFER_SIZE = 100;
    private static final int THREAD_POOL_SIZE = 100;
    private ExecutorService executorService = newFixedThreadPool(THREAD_POOL_SIZE);
    private ConcurrentMap<Integer, JsonResponse[]> pendingResponses = new ConcurrentHashMap<>();
    private BlockingQueue<JsonResponse> responseBuffer = new ArrayBlockingQueue<>(RESPONSE_BUFFER_SIZE);
    private Listener listener = new Listener();
    
    public final void close()
    {
        if (listener.started && !listener.stopped)
            listener.stop();
        
        executorService.shutdownNow();
    }
    
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        WebSocketJsonReader webSocketJsonReader = new WebSocketJsonReader(inputStream);
        
        webSocketJsonReader.setMasked(false);
        
        return (JsonReader) webSocketJsonReader;
    }
    
    @Override
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        WebSocketJsonWriter webSocketJsonWriter = new WebSocketJsonWriter(outputStream);
        
        webSocketJsonWriter.setMasked(true);
        
        return (JsonWriter) webSocketJsonWriter;
    }
    
    
    protected JsonResponse nextJsonResponse()
        throws IOException, InterruptedException
    {
        if (!listener.started)
            listener.start();
        
        return responseBuffer.take();
    }
    
    protected JsonResponse awaitResponse()
        throws IOException, InterruptedException
    {
        JsonResponse response = nextJsonResponse();
        
        if (response.isPending()) {
            log("Got pending response: " + response);
            
            Future<JsonResponse> pendingResponse = listenForResponse(response.getTicketNumber());
            
            while (true) {
                try {
                    response = pendingResponse.get(10, TimeUnit.SECONDS);
                    
                    break;
                }
                
                catch (ExecutionException exception) {
                    log("error while listening for response");
                    
                    throw new IllegalStateException(exception);
                }
                
                catch (TimeoutException exception) {
                    log("waiting...");
                }
            }
        }
        
        return response;
    }
    
    protected Future<JsonResponse> listenForResponse(Integer ticketNumber)
        throws IOException, InterruptedException
    {
        if (!pendingResponses.containsKey(ticketNumber))
            throw new IllegalStateException("ticket number not found");
        
        return executorService.submit(() ->
        {
            JsonResponse[] pendingResponse = pendingResponses.get(ticketNumber);
            
            synchronized (pendingResponse) {
                
                while (pendingResponse[0] == null)
                    pendingResponse.wait();
                
                return pendingResponse[0];
            }
        });
    }
    
    private final JsonResponse nextJsonResponse_()
        throws IOException, InterruptedException
    {
        while (!waitForInput())
            ;
        
        try {
            return JsonResponse.fromReader(jsonReader);
        }
        
        catch (JsonProtocolException exception) {
            // this shouldn't happen
            throw new IllegalStateException("server returned invalid response: " + exception.getMessage());
        }
    }
    
    private final class Listener
    {
        private volatile boolean started = false;
        private volatile boolean stopped = false;
        private Thread thread = new Thread(() ->
        {
            while (!stopped) {
                try {
                    JsonResponse response = nextJsonResponse_();
                    
                    Integer ticketNumber = response.getTicketNumber();
                    
                    if (pendingResponses.containsKey(ticketNumber)) {
                        JsonResponse[] pendingResponse = pendingResponses.get(ticketNumber);
                        
                        synchronized (pendingResponse) {
                            pendingResponse[0] = response;
                            
                            pendingResponse.notifyAll();
                        }
                    }
                    
                    else {
                        if (response.isPending())
                            pendingResponses.put(ticketNumber, new JsonResponse[1]);
                        
                        responseBuffer.put(response);
                        
                    }
                }
                
                catch (InterruptedException exception) {
                    continue;
                }
                    
                catch (IOException exception) {
                    stopped = true;
                }
            }
        });
        
        void start()
        {
            thread.start();
            
            started = true;
        }
        
        void stop()
        {
            stopped = true;
            
            thread.interrupt();
            
            try {
                thread.join();
            }
            
            catch (InterruptedException exception) { }
        }
    }
}
