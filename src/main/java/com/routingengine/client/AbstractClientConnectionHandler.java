package com.routingengine.client;

import static com.routingengine.json.JsonProtocol.EXIT_COMMAND;
import static com.routingengine.json.JsonUtils.castToJsonObject;
import static java.util.concurrent.Executors.newFixedThreadPool;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.routingengine.Agent;
import com.routingengine.Logger;
import com.routingengine.SupportRequest;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonResponse;
import com.routingengine.json.JsonUtils;
import com.routingengine.json.JsonWriter;
import com.routingengine.json.JsonProtocol.ExitConnectionException;
import com.routingengine.json.JsonProtocol.JsonProtocolException;
import com.routingengine.websocket.WebSocketJsonReader;
import com.routingengine.websocket.WebSocketJsonWriter;


public abstract class AbstractClientConnectionHandler extends JsonConnectionHandler
{
    private static final int THREAD_POOL_SIZE = 100;
    private static final int RESPONSE_BUFFER_SIZE = 100;
    private static final long PENDING_TIMEOUT = 10000L;
    private ExecutorService executorService = newFixedThreadPool(THREAD_POOL_SIZE);
    private BlockingQueue<JsonResponse> responseBuffer = new ArrayBlockingQueue<>(RESPONSE_BUFFER_SIZE);
    private ConcurrentMap<Integer, PendingResponse> pendingResponses = new ConcurrentHashMap<>();
    private Listener listener = new Listener();
    
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        WebSocketJsonReader webSocketJsonReader = new WebSocketJsonReader(inputStream);
        
        webSocketJsonReader.setMasked(false);
        
        webSocketJsonReader.initializeReader();
        
        return (JsonReader) webSocketJsonReader;
    }
    
    @Override
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        WebSocketJsonWriter webSocketJsonWriter = new WebSocketJsonWriter(outputStream);
        
        webSocketJsonWriter.setMasked(true);
        
        return (JsonWriter) webSocketJsonWriter;
    }
    
    protected void log(String message)
    {
        Logger.log("Client " + message);
    }
    
    protected void exit()
        throws IOException, ExitConnectionException
    {
        jsonWriter.writeString(EXIT_COMMAND);
        
        jsonWriter.flush();
        
        close();
        
        throw new ExitConnectionException();
    }
    
    public final void close()
    {
        if (listener.started && !listener.stopped)
            listener.stop();
        
        executorService.shutdownNow();
    }
    
    protected final Agent getAgent(JsonResponse response)
    {
        return Agent.fromJson(castToJsonObject(response.getPayload()));
    }
    
    protected final SupportRequest getAssignedSupportRequest(JsonResponse response)
    {
        return getAgent(response).getAssignedSupportRequest();
    }
    
    protected final SupportRequest getSupportRequest(JsonResponse response)
    {
        return SupportRequest.fromJson(castToJsonObject(response.getPayload()));
    }
    
    protected final Agent getAssignedAgent(JsonResponse response)
    {
        return getSupportRequest(response).getAssignedAgent();
    }
    
    protected final void ensureFailedResponseHasErrorPayload(JsonResponse response, String error)
        throws IOException
    {
        String payload = JsonUtils.toString(response.getPayload());
        
        if (!error.matches(payload)) {
            log("got unexpected error: "+ payload);
            
            exit();
        }
    }
    
    protected JsonResponse awaitResponse()
        throws IOException, InterruptedException
    {
        JsonResponse response = nextJsonResponse();
        
        if (response.isPending()) {
            PendingResponse pendingResponse = getPendingResponse(response.getTicketNumber());
            
            while (true) {
                try {
                    response = pendingResponse.getFutureJsonResponse(PENDING_TIMEOUT);
                    
                    if (response.isPending())
                        pendingResponse.reset();
                    
                    else {
                        pendingResponse.remove();
                        
                        break;
                    }
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
    
    protected JsonResponse nextJsonResponse()
        throws IOException, InterruptedException
    {
        if (!listener.started)
            listener.start();
        
        return responseBuffer.take();
    }
    
    protected PendingResponse getPendingResponse(Integer ticketNumber)
    {
        if (!pendingResponses.containsKey(ticketNumber))
            throw new IllegalStateException("ticket number not found");
        
        return pendingResponses.get(ticketNumber);
    }
    
    public final class PendingResponse
    {
        private final Integer ticketNumber;
        private volatile JsonResponse jsonResponse;
        private volatile boolean removed = false;
        
        public PendingResponse(Integer ticketNumber)
        {
            this.ticketNumber = ticketNumber;
            
            jsonResponse = null;
        }
        
        public Integer getTicketNumber()
        {
            return ticketNumber;
        }
        
        public synchronized JsonResponse getJsonResponse()
        {
            return jsonResponse;
        }
        
        public synchronized boolean hasJsonResponse()
        {
            return jsonResponse != null;
        }
        
        public JsonResponse getFutureJsonResponse(long timeoutMillis)
            throws ExecutionException, TimeoutException, InterruptedException
        {
            ensureNotRemoved();
            
            return executorService
                .submit(() -> { return awaitJsonResponse(); })
                .get(timeoutMillis, TimeUnit.MILLISECONDS);
        }
        
        public synchronized JsonResponse awaitJsonResponse()
            throws IOException, InterruptedException
        {
            ensureNotRemoved();
            
            while (jsonResponse == null && !removed)
                wait();
            
            return jsonResponse;
        }
        
        public synchronized void setJsonResponse(JsonResponse jsonResponse)
        {
            ensureNotRemoved();
            
            this.jsonResponse = jsonResponse;
            
            notifyAll();
        }
        
        public synchronized void reset()
        {
            ensureNotRemoved();
            
            jsonResponse = null;
        }
        
        public synchronized void remove()
        {
            ensureNotRemoved();
            
            pendingResponses.remove(ticketNumber);
        }
        
        private synchronized void ensureNotRemoved()
        {
            if (removed)
                throw new IllegalStateException("pending response has been removed");
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
                    JsonResponse response = nextJsonResponse();
                    
                    Integer ticketNumber = response.getTicketNumber();
                    
                    if (pendingResponses.containsKey(ticketNumber)) {
                        PendingResponse pendingResponse = pendingResponses.get(ticketNumber);
                        
                        pendingResponse.setJsonResponse(response);
                    }
                    
                    else {
                        if (response.isPending())
                            pendingResponses.put(ticketNumber, new PendingResponse(ticketNumber));
                        
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
        
        private JsonResponse nextJsonResponse()
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
        
        private void start()
        {
            thread.start();
            
            started = true;
        }
        
        private void stop()
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
