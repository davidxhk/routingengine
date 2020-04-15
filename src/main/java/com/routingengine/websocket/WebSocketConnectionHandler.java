package com.routingengine.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonWriter;


public abstract class WebSocketConnectionHandler extends JsonConnectionHandler
{
    private static final String CHARSET = "UTF-8";
    private static final String DIGEST_ALGORITHM = "SHA-1";
    
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        return new WebSocketJsonReader(inputStream);
    }
    
    @Override
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        return new WebSocketJsonWriter(outputStream);
    }
    
    @Override
    public void connect(Socket socket)
        throws IOException, ConnectionException
    {
        establishHandshake(socket.getInputStream(), socket.getOutputStream());
        
        super.connect(socket);
    }
    
    private final void establishHandshake(InputStream inputStream, OutputStream outputStream)
        throws IOException, WebSocketHandshakeException
    {
        String key = findKey(inputStream);
        
        if (key == null)
            throw new WebSocketHandshakeException("Handshake failed");
        
        String responseKey = generateResponseKey(key);
        
        byte[] response = generateResponse(responseKey);
        
        outputStream.write(response, 0, response.length);
    }
    
    private final String findKey(InputStream inputStream)
    {
        @SuppressWarnings("resource") Scanner scanner = new Scanner(inputStream, CHARSET);
        
        String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
        
        Matcher get = Pattern.compile("^GET").matcher(data);
        
        if (get.find()) {
            Matcher key = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            
            if (key.find())
                return key.group(1);
        }
        
        return null;
    }
    
    private final String generateResponseKey(String key)
    {
        final String SUFFIX = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        
        key += SUFFIX;
        
        byte[] keyBytes = getBytes(key);
        
        byte[] digest = digestBytes(keyBytes);
        
        return Base64.getEncoder().encodeToString(digest);
    }
    
    private final byte[] getBytes(String string)
    {
        try {
            return string.getBytes(CHARSET);
        }
        
        catch (UnsupportedEncodingException exception) {
            throw new IllegalStateException("charset invalid: " + exception.getMessage());
        }
    }
    
    private final byte[] digestBytes(byte[] bytes)
    {
        try {
            return MessageDigest.getInstance(DIGEST_ALGORITHM).digest(bytes);
        }
        
        catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("algorithm invalid: " + exception.getMessage());
        }
    }
    
    private final byte[] generateResponse(String responseKey)
    {
        final String TEMPLATE = (
            "HTTP/1.1 101 Switching Protocols\r\n" +
            "Connection: Upgrade\r\n" +
            "Upgrade: websocket\r\n" +
            "Sec-WebSocket-Accept: %s\r\n\r\n");
        
        String response = String.format(TEMPLATE, responseKey);
        
        return getBytes(response);
    }
    
    public static class WebSocketHandshakeException extends ConnectionException
    {
        public WebSocketHandshakeException()
        {
            super();
        }
        
        public WebSocketHandshakeException(String message)
        {
            super(message);
        }
    }
}
