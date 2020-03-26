package com.routingengine.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.routingengine.client.ConnectionHandler;


public class JsonWriter
    implements Closeable
{
    private final OutputStream outputStream;
    private final OutputStreamWriter outputStreamWriter;
    private final Gson gson;
    
    public JsonWriter(OutputStream outputStream)
    {    
        this.outputStream = outputStream;
        outputStreamWriter = new OutputStreamWriter(outputStream);
        
        gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public final void writeString(String string)
        throws IOException
    {
        outputStreamWriter.write(string);
    }
    
    public final void writeLine(String line)
        throws IOException
    {
        outputStreamWriter.write(line);
        outputStreamWriter.append("\n");
    }
    
    public final void writeJsonObject(JsonObject jsonObject)
        throws IOException
    {
        gson.toJson(jsonObject, outputStreamWriter);
        outputStreamWriter.append("\n");
    }
    
    public final void flush()
        throws IOException
    {
        outputStreamWriter.flush();
    }
    
    @Override
    public void close()
        throws IOException
    {
        ConnectionHandler.closeQuietly(outputStream);
        ConnectionHandler.closeQuietly(outputStreamWriter);
    }
}
