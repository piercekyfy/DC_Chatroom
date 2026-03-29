package common;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import server.Server;

public class MessageBus {
	private Socket socket;
	private boolean error = false;

	private Queue<MessageBuilder> sendQueue = new ArrayDeque<MessageBuilder>();
	
	private List<MessageTask<?>> waitingTasks = new ArrayList<>();
	
	public MessageBus(Socket socket) {
		this.socket = socket;
	}
	

	public void sendMessage(MessageBuilder builder) {
		sendQueue.add(builder);
	}

	public void register(MessageTask<?> task) {
		sendMessage(task.getMessage().serialize());
		waitingTasks.add(task);
	}
	
	public void handle() { // TODO: loop in thread
		if(!sendQueue.isEmpty())
			handleWrite();
		else 
			handleRead();
	}
	
	public boolean hasError() {
		return error;
	}
	
	private void onMessage(MessageHeader header, byte[] content) {
		for(MessageTask<?> task : waitingTasks) {
			if(task.doesExpect(header, ByteBuffer.wrap(content, 0, header.getContentSize()))) {
				task.handleMessage(header, ByteBuffer.wrap(content, 0, header.getContentSize()));
				waitingTasks.remove(task);
				break;
			}
		}
	}
	
	private void handleWrite() {
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
	
	private void handleRead( ) {
		InputStream in = null;
			
		try {
			in = socket.getInputStream();
		
			HeaderParseResult headerResult = StreamUtils.readHeader(in);
			
			if(!headerResult.isSuccess()) {
				if(headerResult.getFailureArgIndex() == 0)
					return;
				else {
					error = true;
					// TODO: invalid header, but the server is never wrong!
					return;
				}
			} else {
				
				byte[] buffer = new byte[headerResult.getValue().getContentSize()]; // TODO: desperately need an array pool
				StreamUtils.read(buffer, in, 0, headerResult.getValue().getContentSize());
				
				onMessage(headerResult.getValue(), buffer);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			error = true;
		}
	}
}
