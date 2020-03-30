package com.routingengine.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class WebSocketStreamReader extends Reader {

  private int byteCount;
  private InputStreamReader inputStreamReader;
  private InputStream in;
  private long bytesToRead;
  private boolean isWholeMessage;
  private int[] mask = new int[4];
  private int maskCounter;
  private boolean headerParsed;

  public WebSocketStreamReader(InputStream in) {
    super(in);
    this.in = in;
    inputStreamReader = new InputStreamReader(in);
    resetState();
  }

  private void resetState() {
    byteCount = 0;
    bytesToRead = 0;
    isWholeMessage = false;
    mask = new int[4];
    maskCounter = 0;
    headerParsed = false;
  }

  @Override
  public int read() throws IOException {
    if (!headerParsed)
      parseHeader();
    byte decodedByte = (byte) ((in.read() & 0xff) ^ (mask[maskCounter++ & 0x3]));
    byteCount++;
    if (byteCount == bytesToRead)
      resetState();
    return ((int) decodedByte);
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    for (int i = 0; i < off; i++) {
      read();
    }
    if (off > bytesToRead)
      return -1;
    int iterations = len > bytesToRead ? (int) bytesToRead : len;
    iterations -= byteCount;
    for (int i = 0; i < iterations; i++) {
      cbuf[i] = (char) read();
    }
    return iterations;
  }

  private void parseHeader() throws IOException {
    if (headerParsed)
      return;
    isWholeMessage = (in.read() & 0xff) == 129;
    bytesToRead = (in.read() & 0xff) - 128;
    if (bytesToRead == 126) {
      bytesToRead = ((in.read() & 0xff) << 8) | (in.read() & 0xff);
    } else if (bytesToRead == 127) {
      bytesToRead = ((in.read() & 0xff) << 56) |
                    ((in.read() & 0xff) << 48) |
                    ((in.read() & 0xff) << 40) |
                    ((in.read() & 0xff) << 32) |
                    ((in.read() & 0xff) << 24) |
                    ((in.read() & 0xff) << 16) |
                    ((in.read() & 0xff) << 8)  |
                    ((in.read() & 0xff));
    }
    for (int i = 0; i < 4; i++) {
      mask[i] = (in.read() & 0xff);
    }
    headerParsed = true;
  }

  @Override
  public boolean ready() throws IOException {
    return inputStreamReader.ready();
  }

  @Override
  public void close() throws IOException {
    inputStreamReader.close();
  }

}
