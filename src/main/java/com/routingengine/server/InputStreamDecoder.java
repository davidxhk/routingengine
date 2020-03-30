package com.routingengine.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamDecoder extends InputStream {

	protected InputStream encoded;
	protected InputStreamReader encodedStreamReader;
	protected InputStream decoded;
	protected InputStreamReader decodedStreamReader;
	private long bytesToRead;
	private boolean isWholeMessage;
	private int[] keys = new int[4];


	public InputStreamDecoder(InputStream encoded) throws IOException {
		this.encoded = encoded;
		encodedStreamReader = new InputStreamReader(this.encoded);
		decodedStreamReader = new InputStreamReader(this.encoded);
	}

	private void decodeStream() throws IOException {
		System.out.println("Decoding InputStream");
		isWholeMessage = (encoded.read() & 0xff) == 129;
		bytesToRead = (encoded.read() & 0xff) - 128;
		System.out.println("Array Size: " + bytesToRead);
			if (bytesToRead == 126) {
				bytesToRead = ((encoded.read() & 0xff) << 8) | (encoded.read() & 0xff);
			}
			else if (bytesToRead == 127) {
				bytesToRead = ((encoded.read() & 0xff) << 56) |
								((encoded.read() & 0xff) << 48) |
								((encoded.read() & 0xff) << 40) |
								((encoded.read() & 0xff) << 32) |
								((encoded.read() & 0xff) << 24) |
								((encoded.read() & 0xff) << 16) |
								((encoded.read() & 0xff) << 8)  |
								((encoded.read() & 0xff) << 0);
			}
			for (int i = 0; i < 4; i++) {
				keys[i] = (encoded.read() & 0xff);

			}
			byte[] decodedByteArray = new byte[(int)bytesToRead]; // Downcasting long to int
			for (int i = 0; i < bytesToRead; i++) {
				decodedByteArray[i] = (byte) ((encoded.read() & 0xff) ^ keys[i & 0x3]);
			}
			System.out.println("Decoded Message: "+ new String(decodedByteArray));
			decoded = new ByteArrayInputStream(decodedByteArray);
			decodedStreamReader = new InputStreamReader(decoded);
	}

	@Override
	public int read() throws IOException {
		if (encodedStreamReader.ready()) {
			decodeStream();
			encoded.skip(encoded.available());
		}
		return decodedStreamReader.read();
	}

	@Override
	public int available() throws IOException {
		if (decodedStreamReader.ready()) return 1;
		else return -1;
	}

	public static InputStream decode(InputStream in) throws IOException{
		boolean isWholeMessage = (in.read() & 0xff) == 129;
		long bytesToRead = (in.read() & 0xff) - 128;
		int[] keys = new int[4];
			if (bytesToRead == 126) {
				bytesToRead = ((in.read() & 0xff) << 8) | (in.read() & 0xff);
			}
			else if (bytesToRead == 127) {
				bytesToRead = ((in.read() & 0xff) << 56) |
								((in.read() & 0xff) << 48) |
								((in.read() & 0xff) << 40) |
								((in.read() & 0xff) << 32) |
								((in.read() & 0xff) << 24) |
								((in.read() & 0xff) << 16) |
								((in.read() & 0xff) << 8)  |
								((in.read() & 0xff) << 0);
			}
			for (int i = 0; i < 4; i++) {
				keys[i] = (in.read() & 0xff);

			}
			byte[] decodedByteArray = new byte[(int)bytesToRead]; // Downcasting long to int
			for (int i = 0; i < bytesToRead; i++) {
				decodedByteArray[i] = (byte) ((in.read() & 0xff) ^ keys[i & 0x3]);
			}
			System.out.println("Decoded Message: "+ new String(decodedByteArray));
			return new ByteArrayInputStream(decodedByteArray);
	}

}
