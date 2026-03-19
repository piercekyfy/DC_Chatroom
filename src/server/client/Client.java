package server.client;

import java.net.*;

import common.HeaderParseResult;
import common.MessageBuilder;
import common.MessageHeader;
import common.ParseResult;
import common.StreamUtils;
import server.Server;

import java.io.*;

// Owner of Client socket connected to a Server.
public class Client {  // TODO: timeouts
	private Socket socket;
	private boolean error = false;

	public Client(Socket socket) {
		this.socket = socket;
	}
	
	public void handle(Server server) { 
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
				
				byte[] buffer = new byte[headerResult.getValue().getTotalSize()];
				StreamUtils.read(buffer, in, 0, headerResult.getValue().getTotalSize());

				server.RouteMessage(this, headerResult.getValue(), buffer);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			error = true;
		} finally {
			try {
				if(in != null)
					in.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
				error = true;
			}
		}
	}
	
	public void close() {
		error = true;
		try {
			socket.close();
		} catch (IOException ex) { }
	}
	
	public void sendMessage(MessageBuilder builder) {
		// TODO: builder-side validation
		
		OutputStream out = null;
		try {
			out = socket.getOutputStream();
			
			byte[] messageBytes = builder.build();
			
			out.write(messageBytes);
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			error = true;
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
				error = true;
			}
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
	
	public InetAddress GetAddress() {
		return socket.getInetAddress();
	}
	
	public String toString() { 
	    return "Client (" + this.GetAddress().getHostAddress() + ")";
	} 
}
