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
import common.models.messages.ErrorMessage;
import common.models.messages.Message;
import common.models.responses.UnrecoverableErrorResponse;

import java.io.*;

// Owner of Client socket connected to a Server.
public class Client {  // TODO: timeouts
	private Socket socket;
	private boolean error = false;
	private boolean requestedDisconnect = false;
	private UserSession session = null;

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
		session.setOnline(false);
		try {
			socket.close();
		} catch (IOException ex) { }
	}
	
	public void sendMessage(Message<?> message) {
		sendQueue.add(message.serialize());
	}
	
	/**
	 * Send a message to output immediately. Very dangerous, should only be used to write errors before closing sockets.
	 * @param message
	 * @throws Exception 
	 */
	public void sendMessageImmediately(Message<?> message) throws Exception {
		OutputStream out = null;
		
		try {
			out = socket.getOutputStream();
			
			out = socket.getOutputStream();

			byte[] messageBytes = message.serialize().build();
			
			out.write(messageBytes);
			
		} catch (Exception ex) {
			error = true;
			throw ex;
		}
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
	
	public void setError() {
		this.error = true;
	}
	
	public boolean requestedDisconnect() {
		return this.requestedDisconnect;
	}
	
	public void requestDisconnect() {
		this.requestedDisconnect = true;
	}
	
	public UserSession getSession() {
		return this.session;
	}
	
	public void setSessionId(UserSession sessionId) {
		this.session = sessionId;
		session.setOnline(true);
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
					error = true;
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
			error = true;
		}
		catch (InvalidContentException ex) {}
		catch (NotFoundException ex) {}
	}
}
