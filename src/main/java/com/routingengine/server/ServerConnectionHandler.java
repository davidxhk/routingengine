package com.routingengine.server;

import static com.routingengine.Logger.log;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonElement;
import com.routingengine.MethodManager;
import com.routingengine.RoutingEngine;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;


<<<<<<< HEAD
public final class ServerConnectionHandler extends JsonConnectionHandler
=======
public final class ServerConnectionHandler extends WebSocketConnectionHandler
>>>>>>> Added InputStreamDecoder
    implements Runnable, Closeable
{
    private final MethodManager methodManager;

    ServerConnectionHandler(Socket socket, RoutingEngine routingEngine)
        throws IOException
    {
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
        // finally {
        //     s.close();
        // }
        connect(socket);
<<<<<<< HEAD
        
        log("Server connected to " + socket.toString());
        
=======

        log("Server connected to " + socket.toString());

>>>>>>> Websocket Handshake
        methodManager = new MethodManager(routingEngine);
    }

    @Override
    public final void runMainLoop()
        throws IOException, InterruptedException, EndConnectionException
    {
        while (!socket.isClosed()) {
            try {
                waitForInput();
            }

            catch (InterruptedException exception) {
                JsonResponse
                    .failure("server connection interrupted")
                    .writeSafe(jsonWriter);

                Thread.currentThread().interrupt();

                throw exception;
            }

            JsonRequest jsonRequest = new JsonRequest();

            try {
                System.out.println("NEW INPUT");
                jsonRequest.read(jsonReader);
                System.out.println(jsonRequest.toString());
            }

            catch (JsonProtocolException exception) {
<<<<<<< HEAD
                log("Server got bad request");
                
=======
                System.out.println(exception.getMessage());
>>>>>>> Websocket Handshake
                JsonResponse
                    .failure(jsonRequest, exception)
                    .writeSafe(jsonWriter);

                continue;
            }

            catch (EndConnectionException exception) {
                jsonWriter.writeLine("Goodbye!");
                jsonWriter.flush();

                throw exception;
            }
<<<<<<< HEAD
            
            log("Server got request –> " + jsonRequest.toString());
            
=======

>>>>>>> Websocket Handshake
            if (jsonRequest.getMethod().matches("new_agent|new_support_request")) {
                if (!jsonRequest.hasArgument("address"))
                    jsonRequest.setArgument("address", getAddress());
            }

            try {
                JsonElement payload = methodManager.handle(
                    jsonRequest.getMethod(),
                    jsonRequest.getArguments());

                JsonResponse
                    .success(jsonRequest, payload)
                    .writeSafe(jsonWriter);
            }

            catch (IllegalArgumentException | IllegalStateException exception) {
                JsonResponse
                    .failure(jsonRequest, exception)
                    .writeSafe(jsonWriter);
            }
        }
    }

    @Override
    public final void run()
    {
<<<<<<< HEAD
=======
        //log("Server handling " + socket.toString());

>>>>>>> Websocket Handshake
        try {
            runMainLoop();
        }

        catch (IOException exception) {
            log("I/O error in " + socket.toString());

            exception.printStackTrace();
        }

        catch (InterruptedException exception) {
            log("Server connection was interrupted");
        }

        catch (EndConnectionException exception) {
            log("Server connection was exited");
        }

        finally {
            close();
        }
    }

    @Override
    public final void close()
    {
        try {
            socket.close();
        }

        catch (IOException e) {
            log("Failed to close " + socket.toString());
        }

        log("Server connection closed");
    }
}
