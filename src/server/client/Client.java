package server.client;

import java.net.*;

import common.Message;
import common.MessageCode;
import common.MessageHeader;
import server.Server;

import java.io.*;

import java.nio.ByteBuffer;

// Owner of Client socket connected to a Server.
public class Client {
	private Socket socket;
	private boolean error = false;
	
	private String username = null;
	private String password = null;
	
	private MessageHeader currentRead = null;
	private Message currentWrite = null;
	
	public Client(Socket socket) {
		this.socket = socket;
	}
	
	private int readInt(InputStream in) throws Exception { // TODO: this is a quick refactor, doesn't behave like it should
		byte[] headerBuffer = new byte[4];
		int read = in.read(headerBuffer, 0, 4);
		
		if(read == 0) {
			throw new Exception();
		} else if (read == -1) {
			error = true;
			throw new Exception();
		}
		
		return ByteBuffer.wrap(headerBuffer).getInt();
	}
	
	public void handle(Server server) {
		System.out.println("Handle 1");
		try {
			
			InputStream in = socket.getInputStream();
			
			int code = readInt(in);
			int count = readInt(in);
			
			int[] sizes = new int[count];
			int total = 0;
			
			for(int i = 0; i < count; i++) {
				sizes[i] = readInt(in);
				total += sizes[i];
			}
			
			byte[] buffer = new byte[total]; // TODO: share/pool buffers
			int read = in.read(buffer);
			
			server.RouteMessage(this, code, sizes, buffer);
			
		} catch (Exception ex) {
			error = true;
			return;
		}
	}
	
	public void setReading(MessageHeader header) {
		currentRead = header;
	}
	
	public MessageHeader getReading() {
		return currentRead;
	}
	
	public Message getWriting() {
		return currentWrite;
	}
	
	public boolean IsLoggedIn() {
		return username != null && password != null;
	}
	
	public boolean IsReadable() {
		if(HasError())
			return false;
		
		try {
			InputStream in = socket.getInputStream();
			return in.available() > 0;
		} catch (IOException io) {
			return false;
		}
	}
	
	public boolean HasError() {
		return socket.isClosed() || error;
	}
	
	public InetAddress GetAddress() {
		return socket.getInetAddress();
	}
	
	public String toString() { 
	    return "Client (" + this.GetAddress().getHostAddress() + ")";
	} 
}
