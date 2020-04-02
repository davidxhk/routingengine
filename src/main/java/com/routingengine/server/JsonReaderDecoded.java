package com.routingengine.server;

import static com.google.gson.JsonParser.parseReader;
import static com.routingengine.json.JsonUtils.castToJsonObject;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routingengine.json.JsonReader;

public class JsonReaderDecoded extends JsonReader {

  private WebSocketStreamReader wSocketStreamReader;

  public JsonReaderDecoded(InputStream inputStream) {
    super(inputStream);
    pipeInputStream();
  }

  @Override
  public boolean ready() throws IOException {
    return wSocketStreamReader.ready();
  }

  public void pipeInputStream() {
    wSocketStreamReader = new WebSocketStreamReader(getInputStream());

    setJsonReader( new com.google.gson.stream.JsonReader(wSocketStreamReader));
    getJsonReader().setLenient(true);
  }

  @Override
  public String readString() throws IOException {
    return getJsonReader().nextString();
  }

  @Override
  public JsonObject parseJsonObject() {
    JsonElement jsonElement = parseReader(getJsonReader());

    return castToJsonObject(jsonElement);
  }

  @Override
  public void clearInputStream() throws IOException {
    while (wSocketStreamReader.ready() && wSocketStreamReader.skip(1) != 1);

    pipeInputStream();
  }


}
