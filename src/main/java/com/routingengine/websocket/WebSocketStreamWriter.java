package com.routingengine.websocket;

import static com.routingengine.websocket.WebSocketProtocol.RANDOM;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WebSocketStreamWriter
{
    public static final String FINBIT = "1000";
    public static final String OPCODE_TEXT = "0001";
    public static final String OPCODE_BINARY = "0010";
    public static final String OPCODE_CLOSE = "1000";
    public static final String OPCODE_PING = "1001";
    public static final String OPCODE_PONG = "1010";
    public static final String MASKBIT = "0";
    public static final byte[] EMPTY_FRAME = {};
    
    public static final byte[] getFrameFormat(byte[] payloadData, boolean masked)
    {
        byte payloadType = (byte) Integer.parseInt(FINBIT + OPCODE_TEXT, 2);
        
        byte[] payloadLength = getPayloadLength(payloadData.length);
        
        try (ByteArrayOutputStream frame = new ByteArrayOutputStream()) {
            
            frame.write(payloadType);
            
            if (!masked)
                frame.write(payloadLength);
            
            else {
                payloadLength[0] += 128;
                
                frame.write(payloadLength);
                
                byte[] mask = new byte[4];
                
                RANDOM.nextBytes(mask);
                
                for (int i = 0; i < payloadData.length; i++)
                    payloadData[i] ^= mask[i % 4];
                
                frame.write(mask);
            }
            
            frame.write(payloadData);
            
            return frame.toByteArray();
        }
        
        catch (IOException exception) { }
        
        return EMPTY_FRAME;
    }
    
    private static byte[] getPayloadLength(int payloadDataLength)
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
}
