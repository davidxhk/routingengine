package com.routingengine.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class WebSocketStreamReader extends Reader
{
    private int byteCount;
    private InputStreamReader inputStreamReader;
    private InputStream in;
    private long bytesToRead;
    private boolean isWholeMessage;
    private int[] mask = new int[4];
    private int maskCounter;
    private boolean headerParsed;
    private boolean open;
    
    public WebSocketStreamReader(InputStream in)
    {
        super(in);
        
        this.in = in;
        
        inputStreamReader = new InputStreamReader(in);
        
        resetState();
        
        open = true;
    }
    
    // Reset state - to use when whole frame is processed
    private void resetState()
    {
        byteCount = 0;
        
        bytesToRead = 0;
        
        isWholeMessage = false;
        
        mask = new int[4];
        
        maskCounter = 0;
        
        headerParsed = false;
    }
    
    private void ensureOpen()
        throws IOException
    {
        if (!open)
            throw new IOException("Stream Closed");
    }
    
    // Reads a byte from the stream and unmasks it with the relevant key
    @Override
    public int read()
        throws IOException
    {
        ensureOpen();
        
        if (!headerParsed)
            parseHeader();
        
        byte decodedByte = (byte) ((in.read() & 0xff) ^ (mask[maskCounter++ & 0x3]));
        
        byteCount++;
        
        if (byteCount == bytesToRead)
            resetState();
        
        return ((int) decodedByte);
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
        
        int firstByte = (in.read() & 0xff);
        
        if (firstByte == 136) {
            close();
            return;
        }
        
        isWholeMessage = firstByte == 129;
        
        bytesToRead = (in.read() & 0xff) - 128;
        
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
        
        for (int i = 0; i < 4; i++)
            mask[i] = (in.read() & 0xff);
        
        headerParsed = true;
    }
    
    @Override
    public boolean ready()
        throws IOException
    {
        if (!open)
            return false;
        
        return inputStreamReader.ready();
    }
    
    @Override
    public void close()
        throws IOException
    {
        inputStreamReader.close();
        
        open = false;
    }
}
