package com.routingengine.websocket;

import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class WebSocketStreamReader extends Reader
{
    private InputStream inputStream;
    private long bytesToRead;
    private long byteCount;
    private boolean masked = false;
    private int[] mask;
    private boolean headerParsed;
    private boolean open;
    
    public WebSocketStreamReader(InputStream inputStream)
    {
        super(inputStream);
        
        this.inputStream = inputStream;
        
        resetState();
        
        open = true;
    }
    
    public void setMasked(boolean masked)
    {
        this.masked = masked;
    }
    
    // Reset state - to use when whole frame is processed
    private void resetState()
    {
        bytesToRead = 0;
        
        byteCount = 0;
        
        mask = new int[4];
        
        headerParsed = false;
    }
    
    private void ensureOpen()
        throws IOException
    {
        if (!open)
            throw new ExitConnectionException("Stream Closed");
    }
    
    // Reads a byte from the stream and unmasks it with the relevant key
    @Override
    public int read()
        throws IOException
    {
        ensureOpen();
        
        if (!headerParsed)
            parseHeader();
        
        if (byteCount == bytesToRead)
            return -1;
        
        byte nextByte = (byte) (inputStream.read() & 0xff);
        
        if (masked)
            nextByte ^= mask[(int) byteCount % 4];
        
        byteCount++;
        
        if (byteCount == bytesToRead)
            resetState();
        
        return ((int) nextByte);
    }
    
    // Reads a specified number of characters into the array and returns the number of characters read
    @Override
    public int read(char[] charArray, int offset, int length)
        throws IOException
    {
        ensureOpen();
        
        if (!headerParsed)
            parseHeader();
        
        long bytesLeft = bytesToRead - byteCount;
        
        if (bytesLeft == 0 || bytesLeft < Integer.toUnsignedLong(offset))
            return -1;
        
        for (int i = 0; i < offset; i++)
            read();
        
        bytesLeft = bytesToRead - byteCount;
        
        int iterations = bytesLeft < Integer.toUnsignedLong(length) ? (int) bytesLeft : length;
        
        for (int i = 0; i < iterations; i++)
            charArray[i] = (char) read();
        
        return iterations;
    }
    
    // Parses the data frame to obtain the length of the message and the mask for the message
    private void parseHeader()
        throws IOException
    {
        if (headerParsed || !ready())
            return;
        
        int firstByte;
        
        while (true) {
            firstByte = (inputStream.read() & 0xff);
            
            if (firstByte == 129)
                break;
            
            if (!ready())
                return;
        }
        
        bytesToRead = (inputStream.read() & 0xff);
        
        if (masked)
            bytesToRead -= 128;
        
        switch ((int) bytesToRead) {
            case 126:
                bytesToRead = ((inputStream.read() & 0xff) << 8) | (inputStream.read() & 0xff);
                break;
            
            case 127:
                bytesToRead = (
                    ((inputStream.read() & 0xff) << 56) |
                    ((inputStream.read() & 0xff) << 48) |
                    ((inputStream.read() & 0xff) << 40) |
                    ((inputStream.read() & 0xff) << 32) |
                    ((inputStream.read() & 0xff) << 24) |
                    ((inputStream.read() & 0xff) << 16) |
                    ((inputStream.read() & 0xff) << 8)  |
                    ((inputStream.read() & 0xff)));
                break;
        }
        
        if (masked) {
            for (int i = 0; i < 4; i++)
                mask[i] = (inputStream.read() & 0xff);
        }
        
        headerParsed = true;
    }
    
    @Override
    public boolean ready()
        throws IOException
    {
        if (!open)
            return false;
        
        return inputStream.available() > 0;
    }
    
    @Override
    public void close()
        throws IOException
    {
        inputStream.close();
        
        open = false;
    }
}
