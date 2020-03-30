package com.routingengine.server;

import java.io.IOException;
import java.net.Socket;
import com.routingengine.client.ConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;

public abstract class WebSocketConnectionHandler extends ConnectionHandler {


    @Override
    public void connect(Socket socket)
        throws IOException
    {
        this.socket = socket;
        jsonReader = new JsonReader( new InputStreamDecoder(socket.getInputStream()));
        jsonWriter = new JsonWriter(socket.getOutputStream());
    }
}
