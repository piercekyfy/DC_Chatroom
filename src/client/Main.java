package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Socket s = new Socket();
		
		s.connect(new InetSocketAddress("127.0.0.1", 6540));;
		
		OutputStream out = s.getOutputStream();
		
		System.out.println("Send 1");
		
		byte[] msg = StandardCharsets.UTF_8.encode("Hello world!").array();
		
		// [code (int)] [count (int)]  [size (int), size(int), ...]
		byte[] header = ByteBuffer.allocate(Integer.BYTES * 4)
		    .putInt(1)
		    .putInt(2)
		    .putInt(Integer.BYTES)
		    .putInt(msg.length)
		    .array();
		
		// [bytes (of size[i]), bytes, ...]
		byte[] content = ByteBuffer.allocate(Integer.BYTES + msg.length)
			.putInt(11)
		    .put(msg)
		    .array();

		byte[] message = ByteBuffer.allocate(header.length + content.length)
		    .put(header)
		    .put(content)
		    .array();
		
		out.write(message);
		out.flush();
	
		System.out.println("Send 2");

	}

}
