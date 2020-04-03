package com.routingengine.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.routingengine.json.JsonWriter;

public class JsonWriterEncoded extends JsonWriter{

  final static String FINBIT = "1000";
  final static String OPCODE_TEXT = "0001";
  final static String OPCODE_BINARY = "0010";
  final static String OPCODE_CLOSE = "1000";
  final static String OPCODE_PING = "1001";
  final static String OPCODE_PONG = "1010";
  final static String MASKBIT = "0";
  final static byte[] EMPTY_FRAME = {};

  private OutputStream out;

  /**
   * @param outputStream Outputstream to be wrapped and processed
   */
  public JsonWriterEncoded(OutputStream outputStream) {
    super(outputStream);
    this.out = outputStream;
  }


  /**
   * Formats the string into a byte array that is compliant with
   * the Websocket Base Framing Protocol
   * @param string
   * @return byte[]
   */
  public byte[] getFrameFormat(String string) {
    try {
      byte[] payloadData = string.getBytes();
      int payloadDataLength = payloadData.length;
      byte payloadInformation = (byte) ((Integer.parseInt(FINBIT, 2) << 4) | (Integer.parseInt(OPCODE_TEXT,2)));
      byte[] payloadLengthInformation;
      if (payloadDataLength <= 125) {
        payloadLengthInformation = new byte[] {(byte) payloadDataLength};
      } else if (payloadDataLength <= Math.pow(2, 16)) {
        payloadLengthInformation = new byte[] {
          (byte) 126,
          (byte) (payloadDataLength >> 8),
          (byte) payloadDataLength
        };
      } else {
        payloadLengthInformation = new byte[] {
          (byte) 127,
          (byte) (payloadDataLength >> 56),
          (byte) (payloadDataLength >> 48),
          (byte) (payloadDataLength >> 40),
          (byte) (payloadDataLength >> 32),
          (byte) (payloadDataLength >> 24),
          (byte) (payloadDataLength >> 16),
          (byte) (payloadDataLength >> 8),
          (byte) payloadDataLength
        };
      }
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byteArrayOutputStream.write(payloadInformation);
      byteArrayOutputStream.write(payloadLengthInformation);
      byteArrayOutputStream.write(payloadData);
      byte[] result = byteArrayOutputStream.toByteArray();
      byteArrayOutputStream.close();
      return result;
    } catch (Exception e) {
      System.out.println("Unable to convert message to frame format");
      System.out.println("Returning Empty Frame..");
    }
    return EMPTY_FRAME;
  }


  /**
   * @param string
   * @throws IOException
   */
  @Override
  public void writeString(String string) throws IOException {
    byte[] frameFormat = getFrameFormat(string);
    if (frameFormat.equals(EMPTY_FRAME)) return;
    out.write(frameFormat);
  }


  /**
   * @param line
   * @throws IOException
   */
  @Override
  public void writeLine(String line) throws IOException {
    String newLine = line + "\n";
    byte[] frameFormat = getFrameFormat(newLine);
    if (frameFormat.equals(EMPTY_FRAME)) return;
    out.write(frameFormat);
  }


  /**
   * @param jsonObject
   * @throws IOException
   */
  @Override
  public void writeJsonObject(JsonObject jsonObject) throws IOException{
    Gson gson = getGson();
    String json = gson.toJson(jsonObject) + "\n";
    byte[] frameFormat = getFrameFormat(json);
    if (frameFormat.equals(EMPTY_FRAME)) return;
    out.write(frameFormat);
  }





}
