package com.routingengine.json;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public abstract class JsonConnectionHandler
{
    protected Socket socket = null;
    protected JsonReader jsonReader = null;
    protected JsonWriter jsonWriter = null;
    public static final int SLEEP_MILLIS = 10;
    
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
        while (!jsonReader.ready())
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
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
