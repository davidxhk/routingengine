package com.routingengine.websocket;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebSocketProtocol
{
    public static final Random RANDOM = new SecureRandom();
    public static final Base64.Encoder ENCODER = Base64.getEncoder();
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String NEWLINE = "\r\n";
    
    public static final void doOpeningHandshake(Socket socket)
        throws IOException, WebSocketProtocolException
    {
        OpeningHandshake openingHandshake = new OpeningHandshake(socket.getInetAddress().getHostAddress());
        
        byte[] handshakeBytes = openingHandshake.toBytes();
        
        socket.getOutputStream().write(handshakeBytes, 0, handshakeBytes.length);
        
        socket.getOutputStream().flush();
        
        Scanner scanner = new Scanner(socket.getInputStream(), CHARSET);
        
        String data = scanner.useDelimiter(NEWLINE + NEWLINE).next();
        
        Matcher matcher = Pattern.compile("Sec-WebSocket-Accept: (.*)").matcher(data);
        
        String acceptKey = matcher.find() ? matcher.group(1) : null;
        
        if (acceptKey == null || !acceptKey.equals(openingHandshake.acceptKey))
            throw new WebSocketProtocolException("Opening handshake failed");
    }
    
    public static final void doClosingHandshake(Socket socket)
        throws IOException, WebSocketProtocolException
    {
        Scanner scanner = new Scanner(socket.getInputStream(), CHARSET);
        
        String data = scanner.useDelimiter(NEWLINE + NEWLINE).next();
        
        Matcher matcher = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
        
        String key = matcher.find() ? matcher.group(1) : null;
        
        if (key == null)
            throw new WebSocketProtocolException("Closing handshake failed");
        
        ClosingHandshake closingHandshake = new ClosingHandshake(key);
        
        byte[] handshakeBytes = closingHandshake.toBytes();
        
        socket.getOutputStream().write(handshakeBytes, 0, handshakeBytes.length);
        
        socket.getOutputStream().flush();
    }
    
    public static class OpeningHandshake
    {
        public static final MessageDigest SHA_1;
        
        static {
            try {
                SHA_1 = MessageDigest.getInstance("SHA-1");
            }
            
            catch (NoSuchAlgorithmException exception) {
                // Shouldn't happen: SHA-1 must be available in every Java platform
                // implementation
                throw new InternalError("Minimum requirements", exception);
            }
        }
        
        public static final String KEY_SUFFIX = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        
        public final String host;
        public final String key;
        public final String acceptKey;
        
        public OpeningHandshake(String host)
        {
            this.host = host;
            
            byte[] keyBytes = new byte[16];
            
            RANDOM.nextBytes(keyBytes);
            
            key = ENCODER.encodeToString(keyBytes);
            
            acceptKey = getAcceptKey(key);
        }
        
        public static final String getAcceptKey(String key)
        {
            synchronized (SHA_1) {
                SHA_1.update((key + KEY_SUFFIX).getBytes(CHARSET));
                
                return ENCODER.encodeToString(SHA_1.digest());
            }
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            
            builder.append("GET HTTP/1.1" + NEWLINE);
            builder.append("Host: " + host + NEWLINE);
            builder.append("Upgrade: websocket" + NEWLINE);
            builder.append("Connection: Upgrade" + NEWLINE);
            builder.append("Sec-WebSocket-Key: " + key + NEWLINE);
            builder.append("Sec-WebSocket-Version: 13" + NEWLINE + NEWLINE);
            
            return builder.toString();
        }
        
        public byte[] toBytes()
        {
            return toString().getBytes(CHARSET);
        }
    }
    
    public static class ClosingHandshake
    {
        public final String acceptKey;
        
        public ClosingHandshake(String key)
        {
            acceptKey = OpeningHandshake.getAcceptKey(key);
        }
        
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            
            builder.append("HTTP/1.1 101 Switching Protocols" + NEWLINE);
            builder.append("Upgrade: websocket" + NEWLINE);
            builder.append("Connection: Upgrade" + NEWLINE);
            builder.append("Sec-WebSocket-Accept: " + acceptKey + NEWLINE + NEWLINE);
            
            return builder.toString();
        }
        
        public byte[] toBytes()
        {
            return toString().getBytes(CHARSET);
        }
    }
    
    public static class WebSocketProtocolException extends Exception
    {
        public WebSocketProtocolException()
        {
            super();
        }
        
        public WebSocketProtocolException(String message)
        {
            super(message);
        }
    }
}
