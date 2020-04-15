package com.routingengine.websocket;

import java.io.InputStream;
import java.io.Reader;
import com.routingengine.json.JsonReader;


public class WebSocketJsonReader extends JsonReader
{
    public WebSocketJsonReader(InputStream inputStream)
    {
        super(inputStream);
    }
    
    @Override
    protected Reader getReader(InputStream inputStream)
    {
        return new WebSocketStreamReader(inputStream);
    }
}
