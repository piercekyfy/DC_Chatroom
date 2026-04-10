package server;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import common.ErrorDefs;
import common.HeaderParseResult;
import common.MessageBus;
import common.MessageSerializer;
import common.MessageDefs;
import common.MessageHandler;
import common.ParseResult;
import common.StreamUtils;
import common.models.UserSession;
import common.models.messages.ErrorMessage;
import common.models.messages.Message;
import common.models.messages.MessageHeader;
import common.models.responses.UnrecoverableErrorResponse;

import java.io.*;

public class Client { 
	private MessageBus bus;
	
	private UserSession session = null;
	private boolean requestedDisconnect = false;
	
	private boolean clientError = false;
	
	public Client(Socket socket, Server server) throws SocketException {
		socket.setSoTimeout(50);
		bus = new MessageBus(socket);
		
		ConsumeAllMessages consumeAllHandler = new ConsumeAllMessages();
		consumeAllHandler.register((header, content) -> server.routeMessage(this, header, content));
		bus.register(consumeAllHandler);
	}
	
	public void handle(Server server) { 
		bus.handle();
	}
	
	public void close() {
		if(session != null)
			session.setOnline(false);
		
		bus.close();
	}
	
	public void send(Message message) {
		bus.send(message);
	}
	
	public void sendImmediately(Message message) {
		bus.writeImmediately(message.serialize());
	}
	
	public void setError() {
		clientError = true;
	}
	
	public boolean hasError() {
		return clientError || bus.hasError();
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
	
	public String toString() { 
	    return "Client (" + bus.getAddress().getHostAddress() + ")";
	} 
}
