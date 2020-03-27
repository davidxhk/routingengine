package com.routingengine.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class JsonReader
{
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private com.google.gson.stream.JsonReader jsonReader;
    
    public JsonReader(InputStream inputStream)
    {        
        this.inputStream = inputStream;
        pipeInputStream();
    }
    
    public final boolean ready()
        throws IOException
    {
        return inputStreamReader.ready();
    }

    public final void pipeInputStream()
    {
        inputStreamReader = new InputStreamReader(inputStream);
        
        jsonReader = new com.google.gson.stream.JsonReader(inputStreamReader);
        jsonReader.setLenient(true);
    }
    
    public final String readString()
        throws IOException
    {
        return jsonReader.nextString();
    }
    
    public final JsonObject parseJsonObject()
    {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        
        return JsonUtils.castToJsonObject(jsonElement);
    }
    
    public final void clearInputStream()
        throws IOException
    {
        while (inputStreamReader.ready() && inputStreamReader.skip(1) != 1)
            ;
        
        pipeInputStream();
    }
}
