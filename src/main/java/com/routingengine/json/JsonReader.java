package com.routingengine.json;

import static com.google.gson.JsonParser.parseReader;
import static com.routingengine.json.JsonUtils.castToJsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


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
        JsonElement jsonElement = parseReader(jsonReader);
        
        return castToJsonObject(jsonElement);
    }
    
    public final void clearInputStream()
        throws IOException
    {
        while (inputStreamReader.ready() && inputStreamReader.skip(1) != 1)
            ;
        
        pipeInputStream();
    }
}
