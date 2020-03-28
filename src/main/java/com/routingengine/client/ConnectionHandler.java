package com.routingengine.client;

import java.io.IOException;
import java.net.Socket;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;


public abstract class ConnectionHandler
{
    protected Socket socket = null;
    protected JsonReader jsonReader = null;
    protected JsonWriter jsonWriter = null;
    
    public void connect(Socket socket)
        throws IOException
    {
        this.socket = socket;
        
        jsonReader = new JsonReader(socket.getInputStream());
        jsonWriter = new JsonWriter(socket.getOutputStream());
    }
    
    public final String getAddress()
    {
        return socket.getInetAddress().getHostAddress();
    }
    
    protected final void waitForInput()
        throws IOException, InterruptedException
    {
        while (!jsonReader.ready()) {
            
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }
    
    public abstract void runMainLoop() throws IOException, InterruptedException, EndConnectionException;
    
    public static class EndConnectionException extends Exception
    {
        public EndConnectionException()
        {
            super();
        }
        
        public EndConnectionException(String message)
        {
            super(message);
        }
    }
}
