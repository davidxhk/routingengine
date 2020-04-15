package com.routingengine.json;

import static com.google.gson.JsonParser.parseReader;
import static com.routingengine.json.JsonUtils.castToJsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class JsonReader
{
    private final InputStream inputStream;
    protected Reader reader;
    private com.google.gson.stream.JsonReader jsonReader;
    
    public JsonReader(InputStream inputStream)
    {
        this.inputStream = inputStream;
        
        initializeReader();
    }
    
    protected Reader getReader(InputStream inputStream)
    {
        return new InputStreamReader(inputStream);
    }
    
    public final void initializeReader()
    {
        reader = getReader(inputStream);
        
        jsonReader = new com.google.gson.stream.JsonReader(reader);
        
        jsonReader.setLenient(true);
    }
    
    public final boolean ready()
        throws IOException
    {
        return reader.ready();
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
        while (reader.ready() && reader.skip(1) != 1)
            ;
        
        initializeReader();
    }
}
