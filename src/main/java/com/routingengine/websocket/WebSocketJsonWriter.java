package com.routingengine.websocket;

import static com.routingengine.websocket.WebSocketStreamWriter.getFrameFormat;
import static com.routingengine.websocket.WebSocketStreamWriter.EMPTY_FRAME;
import java.io.IOException;
import java.io.OutputStream;
import com.routingengine.json.JsonWriter;


public class WebSocketJsonWriter extends JsonWriter
{
    private boolean masked;
    
    public WebSocketJsonWriter(OutputStream outputStream)
    {
        super(outputStream);
    }
    
    public void setMasked(boolean masked)
    {
        this.masked = masked;
    }
    
    @Override
    public void write(byte[] bytes)
        throws IOException
    {
        byte[] frameFormat = getFrameFormat(bytes, masked);
        
        if (EMPTY_FRAME.equals(frameFormat))
            return;
        
        super.write(frameFormat);
    }
}
