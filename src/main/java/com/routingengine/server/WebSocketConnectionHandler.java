package com.routingengine.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.routingengine.json.JsonConnectionHandler;

public abstract class WebSocketConnectionHandler extends JsonConnectionHandler {


    @Override
    @SuppressWarnings({ "resource" })
    public void connect(Socket socket)
        throws IOException
    {
        // Handshake Protocol
        InputStream in = socket.getInputStream();
        OutputStream out  = socket.getOutputStream();
        Scanner s = new Scanner(in, "UTF-8");
        try {
            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                    + "\r\n\r\n").getBytes("UTF-8");
                out.write(response, 0, response.length);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.socket = socket;
        jsonReader = new JsonReaderDecoded(socket.getInputStream());
        jsonWriter = new JsonWriterEncoded(socket.getOutputStream());
    }
}
