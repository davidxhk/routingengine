package com.routingengine.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;


public abstract class ConnectionHandler
{
    protected Socket socket = null;
    protected JsonReader jsonReader = null;
    protected JsonWriter jsonWriter = null;
    
    public final void connect(Socket socket)
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
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }
    
    public abstract void runMainLoop() throws IOException, InterruptedException;
}
