package com.routingengine.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.routingengine.json.JsonWriter;


public class WebSocketJsonWriter extends JsonWriter
{
    private static final String FINBIT = "1000";
    private static final String OPCODE_TEXT = "0001";
    //private static final String OPCODE_BINARY = "0010";
    //private static final String OPCODE_CLOSE = "1000";
    //private static final String OPCODE_PING = "1001";
    //private static final String OPCODE_PONG = "1010";
    //private static final String MASKBIT = "0";
    private static final byte[] EMPTY_FRAME = {};
    
    public WebSocketJsonWriter(OutputStream outputStream)
    {
        super(outputStream);
    }
    
    // Formats the string into a byte array that is compliant with the Websocket Base Framing Protocol
    private final byte[] getFrameFormat(byte[] payloadData)
    {
        try {
            byte payloadType = (byte) Integer.parseInt(FINBIT + OPCODE_TEXT, 2);
            
            byte[] payloadLength = getPayloadLength(payloadData.length);
            
            ByteArrayOutputStream frame = new ByteArrayOutputStream();
            
            frame.write(payloadType);
            
            frame.write(payloadLength);
            
            frame.write(payloadData);
            
            byte[] result = frame.toByteArray();
            
            frame.close();
            
            return result;
        }
        
        catch (Exception exception) {
            System.out.println("Unable to convert message to frame format");
            
            System.out.println("Returning Empty Frame..");
        }
        
        return EMPTY_FRAME;
    }
    
    private byte[] getPayloadLength(int payloadDataLength)
    {
        if (payloadDataLength <= 125)
            return new byte[] {(byte) payloadDataLength};
        
        if (payloadDataLength <= 65536)
            return new byte[] {(byte) 126, (byte) (payloadDataLength >> 8), (byte) payloadDataLength};
        
        return new byte[] {
            (byte) 127,
            (byte) (payloadDataLength >> 56),
            (byte) (payloadDataLength >> 48),
            (byte) (payloadDataLength >> 40),
            (byte) (payloadDataLength >> 32),
            (byte) (payloadDataLength >> 24),
            (byte) (payloadDataLength >> 16),
            (byte) (payloadDataLength >> 8),
            (byte) (payloadDataLength)
        };
    }
    
    @Override
    public void write(byte[] bytes)
        throws IOException
    {
        byte[] frameFormat = getFrameFormat(bytes);
        
        if (frameFormat.equals(EMPTY_FRAME))
            return;
        
        super.write(frameFormat);
    }
}
