package server;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import common.HeaderParseResult;
import common.MessageBuilder;
import common.MessageHeader;
import common.ParseResult;
import common.StreamUtils;

import java.io.*;

// Owner of Client socket connected to a Server.
public class Client {  // TODO: timeouts
	private Socket socket;
	private boolean error = false;

	private Queue<MessageBuilder> sendQueue = new ArrayDeque<MessageBuilder>();

	public Client(Socket socket) throws SocketException {
		this.socket = socket;
		this.socket.setSoTimeout(100);
	}
	
	public void handle(Server server) { 
		if(!sendQueue.isEmpty())
			handleWrite(server);
		else 
			handleRead(server);
	}
	
	public void close() {
		error = true;
		try {
			socket.close();
		} catch (IOException ex) { }
	}
	
	public void sendMessage(MessageBuilder builder) {
		// TODO: builder-side validation
		
		sendQueue.add(builder);
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
	
	private void handleWrite(Server server) {
		OutputStream out = null;
		
		try {
			out = socket.getOutputStream();
			
			MessageBuilder message = sendQueue.poll();
			
			byte[] messageBytes = message.build();
			
			out.write(messageBytes);
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			error = true;
		}
	}
	
	private void handleRead(Server server) {
		InputStream in = null;
			
		try {
			in = socket.getInputStream();
		
			HeaderParseResult headerResult = StreamUtils.readHeader(in);
			
			if(!headerResult.isSuccess()) {
				if(headerResult.getFailureArgIndex() == 0)
					return;
				else {
					error = true;
					sendMessage(new MessageBuilder().setAsInvalidHeaderArg(headerResult.getFailureArgIndex()));
					return;
				}
			} else {
				
				byte[] buffer = new byte[headerResult.getValue().getContentSize()];
				StreamUtils.read(buffer, in, 0, headerResult.getValue().getContentSize());

				server.RouteMessage(this, headerResult.getValue(), buffer);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			error = true;
		}
	}
}
