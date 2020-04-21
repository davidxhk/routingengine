package com.routingengine.client;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import static com.routingengine.websocket.WebSocketProtocol.doOpeningHandshake;
import static com.routingengine.websocket.WebSocketProtocol.WebSocketProtocolException;
import java.io.IOException;
import java.net.Socket;


public final class Client
    implements Runnable
{
    private Socket socket;
    private ClientConnectionHandler connectionHandler = null;
    private boolean handshakeEstablished = false;
    
    public Client(String hostname, int port)
        throws IOException
    {
        socket = new Socket(hostname, port);
        
        log("Client connected to " + socket.toString());
    }
    
    public final Client setConnectionHandler(ClientConnectionHandler clientConnectionHandler)
        throws IOException
    {
        if (connectionHandler != null)
            connectionHandler.close();
        
        clientConnectionHandler.connect(socket);
        
        this.connectionHandler = clientConnectionHandler;
        
        return this;
    }
    
    @Override
    public final void run()
    {
        if (connectionHandler == null)
            throw new IllegalStateException("connection handler missing");
        
        if (socket == null)
            throw new IllegalStateException("socket missing!");
        
        else if (socket.isClosed())
            throw new IllegalStateException("socket closed");
        
        try {
            if (!handshakeEstablished)
                establishHandshake();
            
            connectionHandler.runMainLoop();
        }
        
        catch (InterruptedException exception) {
            log("Client was interrupted");
        }
        
        catch (ExitConnectionException exception) {
            log("Client exiting");
            
            close();
        }
        
        catch (IOException exception) {
            log("I/O exception: " + exception.getMessage());
            
            close();
        }
    }
    
    private final void establishHandshake()
        throws IOException
    {
        try {
            doOpeningHandshake(socket);
        }
        
        catch (WebSocketProtocolException exception) {
            log("WebSocket exception: " + exception.getMessage());
            
            return;
        }
        
        handshakeEstablished = true;
    }
    
    private final void close()
    {
        if (connectionHandler != null)
            connectionHandler.close();
        
        try {
            socket.close();
        }
        
        catch (IOException e) {
            log("Failed to close " + socket.toString());
        }
        
        log("Client connection closed");
    }
}
