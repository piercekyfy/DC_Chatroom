package server;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import common.ErrorDefs;
import common.HeaderParseResult;
import common.MessageSerializer;
import common.MessageDefs;
import common.MessageHeader;
import common.ParseResult;
import common.StreamUtils;
import common.models.UserSession;
import common.models.messages.AnyErrorMessage;

import java.io.*;

// Owner of Client socket connected to a Server.
public class Client {  // TODO: timeouts
	private Socket socket;
	private boolean error = false;
	private UserSession sessionId = null;

	private Queue<MessageSerializer> sendQueue = new ArrayDeque<MessageSerializer>();

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
	
	public void sendMessage(MessageSerializer builder) {
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
	
	public UserSession getSession() {
		return this.sessionId;
	}
	
	public void setSessionId(UserSession sessionId) {
		this.sessionId = sessionId;
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
			
			MessageSerializer message = sendQueue.poll();
			
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
					error = true; // TODO: new way of erroring
					// Unrecoverable error (specifies no source)
					sendMessage(new AnyErrorMessage(MessageDefs.INVALID_HEADER_ERROR, ErrorDefs.INVALID_OR_MISSING_ARG, -1).serialize());
					return;
				}
			} else {
				
				byte[] buffer = new byte[headerResult.getValue().getContentSize()];
				StreamUtils.read(buffer, in, 0, headerResult.getValue().getContentSize());


				server.RouteMessage(this, headerResult.getValue(), buffer);
			}
		} 
		catch(SocketTimeoutException ex) {}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
			error = true;
		}
		catch (InvalidContentException ex) {}
		catch (NotFoundException ex) {}
	}
}
