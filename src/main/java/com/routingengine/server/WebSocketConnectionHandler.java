package com.routingengine.server;

import java.io.IOException;
import java.net.Socket;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;

public abstract class WebSocketConnectionHandler extends JsonConnectionHandler {


    @Override
    public void connect(Socket socket)
        throws IOException
    {
        this.socket = socket;
        jsonReader = new JsonReaderDecoded(socket.getInputStream());
        jsonWriter = new JsonWriterEncoded(socket.getOutputStream());
    }
}
