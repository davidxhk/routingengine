package com.routingengine.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class JsonWriter
{
    private final OutputStreamWriter outputStreamWriter;
    private final Gson gson;
    
    public JsonWriter(OutputStream outputStream)
    {    
        outputStreamWriter = new OutputStreamWriter(outputStream);
        
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
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
}
