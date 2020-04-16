package com.routingengine.websocket;

import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class WebSocketStreamReader extends Reader
{
    private InputStream in;
    private long bytesToRead;
    private int byteCount;
    private boolean masked = false;
    private int[] mask;
    private boolean headerParsed;
    private boolean open;
    
    public WebSocketStreamReader(InputStream in)
    {
        super(in);
        
        this.in = in;
        
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
        
        byte nextByte = (byte) (in.read() & 0xff);
        
        if (masked)
            nextByte ^= mask[byteCount % 4];
        
        byteCount++;
        
        if (byteCount == bytesToRead)
            resetState();
        
        return ((int) nextByte);
    }
    
    // Reads a specified number of characters into the array and returns the number of characters read
    @Override
    public int read(char[] cbuf, int off, int len)
        throws IOException
    {
        ensureOpen();
        
        if (!headerParsed)
            parseHeader();
        
        if (off > bytesToRead)
            return -1;
        
        for (int i = 0; i < off; i++)
            read();
        
        int iterations = len > bytesToRead ? (int) bytesToRead : len;
        
        iterations -= byteCount;
        
        for (int i = 0; i < iterations; i++)
            cbuf[i] = (char) read();
        
        return iterations;
    }
    
    // Parses the data frame to obtain the length of the message and the mask for the message
    private void parseHeader()
        throws IOException
    {
        if (headerParsed)
            return;
        
        int firstByte;
        
        while (true) {
            firstByte = (in.read() & 0xff);
            
            if (firstByte == 129)
                break;
            
            if (in.available() == 0)
                return;
        }
        
        bytesToRead = (in.read() & 0xff);
        
        if (masked)
            bytesToRead -= 128;
        
        switch ((int) bytesToRead) {
            case 126:
                bytesToRead = ((in.read() & 0xff) << 8) | (in.read() & 0xff);
                break;
            
            case 127:
                bytesToRead = (
                    ((in.read() & 0xff) << 56) |
                    ((in.read() & 0xff) << 48) |
                    ((in.read() & 0xff) << 40) |
                    ((in.read() & 0xff) << 32) |
                    ((in.read() & 0xff) << 24) |
                    ((in.read() & 0xff) << 16) |
                    ((in.read() & 0xff) << 8)  |
                    ((in.read() & 0xff)));
                break;
        }
        
        if (masked) {
            for (int i = 0; i < 4; i++)
                mask[i] = (in.read() & 0xff);
        }
        
        headerParsed = true;
    }
    
    @Override
    public boolean ready()
        throws IOException
    {
        if (!open)
            return false;
        
        return in.available() > 0;
    }
    
    @Override
    public void close()
        throws IOException
    {
        in.close();
        
        open = false;
    }
}
