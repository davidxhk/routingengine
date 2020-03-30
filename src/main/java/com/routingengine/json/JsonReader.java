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
    private final InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private com.google.gson.stream.JsonReader jsonReader;

    public JsonReader(InputStream inputStream)
    {
        this.inputStream = inputStream;
        reinitialize();
    }

    public boolean ready()
        throws IOException
    {
        return inputStreamReader.ready();
    }

    public final void reinitialize()
    {
        inputStreamReader = new InputStreamReader(inputStream);

        jsonReader = new com.google.gson.stream.JsonReader(inputStreamReader);
        jsonReader.setLenient(true);
    }

    public String readString()
        throws IOException
    {
        return jsonReader.nextString();
    }

    public JsonObject parseJsonObject()
    {
        JsonElement jsonElement = parseReader(jsonReader);

        return castToJsonObject(jsonElement);
    }

    public void clearInputStream()
        throws IOException
    {
        while (inputStreamReader.ready() && inputStreamReader.skip(1) != 1)
            ;

        reinitialize();
    }

    protected void setJsonReader(com.google.gson.stream.JsonReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    protected com.google.gson.stream.JsonReader getJsonReader() {
        return jsonReader;
    }

    protected InputStream getInputStream() {
        return inputStream;
    }
}
