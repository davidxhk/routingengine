package com.routingengine.json;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonWriter
{
    private final OutputStream outputStream;
    private OutputStreamWriter outputStreamWriter;
    private BufferedWriter bufferedWriter;
    private final Gson gson;
    private static final int BUFFER_SIZE = 8192;
    
    public JsonWriter(OutputStream outputStream)
    {
        this.outputStream = outputStream;
        pipeOutputStream();
        
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    }
    
    public final void pipeOutputStream()
    {
        outputStreamWriter = new OutputStreamWriter(outputStream);
        bufferedWriter = new BufferedWriter(outputStreamWriter, BUFFER_SIZE);
    }
    
    public void writeString(String string)
        throws IOException
    {
        bufferedWriter.write(string);
    }
    
    public void writeLine(String line)
        throws IOException
    {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
    }
    
    public void writeJsonObject(JsonObject jsonObject)
        throws IOException
    {
        gson.toJson(jsonObject, bufferedWriter);
        bufferedWriter.append("\n");
    }
    
    public final void flush()
        throws IOException
    {
        bufferedWriter.flush();
    }
}
