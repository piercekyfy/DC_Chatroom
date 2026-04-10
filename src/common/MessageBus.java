package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import common.models.messages.Message;
import common.models.messages.MessageHeader;

public class MessageBus {
	private Socket socket;
	
	private Queue<MessageSerializer> sendQueue = new ArrayDeque<MessageSerializer>();
	private List<MessageHandler> handlers = new ArrayList<>();
	private ByteBufferPool bufferPool = new ByteBufferPool();
	private boolean error = false;

	public MessageBus(Socket socket) {
		this.socket = socket;
	}
	
	/**
	 * Send a queued message, or if no messages are queued to be sent, read the next message.
	 */
	public void handle() {
		if(!sendQueue.isEmpty())
			writeOne();
		else 
			readOne();
	}
	
	public void send(Message message) {
		synchronized(sendQueue) {
			sendQueue.add(message.serialize());
		}
	}
	
	/**
	 * Register a MessageTask to send a message and listen for a reply.
	 */
	public void register(MessageTask task) {
		synchronized(handlers) {
			handlers.add(task);
		}

		send(task.getMessage());
	}
	
	public void register(MessageHandler handler) {
		synchronized(handlers) {
			handlers.add(handler);
		}
	}

	/**
	 * Immediately write a message from this bus, bypassing the internal queue.
	 * This should only be used internally, or to send errors before closing the bus.
	 * @return True if a message was successfully sent, false otherwise.
	 */
	public boolean writeImmediately(MessageSerializer serializedMessage) {
		OutputStream out = null;
		ByteBuffer buffer = null;
		try {
			out = socket.getOutputStream();
			
			buffer = serializedMessage.rentBuild(bufferPool);
			
			out.write(buffer.array(), buffer.position(), buffer.remaining());
			
			return true;
		} catch (Exception ex) {
			error = true;
			return false;
		} finally {
			if(buffer != null)
				bufferPool.release(buffer);
		}
	}

	public void close() {
		error = true;
		
		try {
			while(writeOne()) {} // Write until failure.
			
			// Try flush
			socket.getOutputStream().flush();
			
			socket.close();
		} catch (IOException ex) {}
		
		for(MessageHandler handler : handlers) {
			handler.handleStopped();
		}
	}
	
	public boolean hasError() {
		return error || socket.isClosed();
	}
	
	public InetAddress getAddress() {
		return socket.getInetAddress();
	}
	
	/** 
	 * Write the next message in the send queue to the socket's output stream.
	 * @return True if a message was successfully written, false otherwise.
	 */
	private boolean writeOne() {
		
		MessageSerializer message;
		synchronized(sendQueue) {
			if(sendQueue.isEmpty()) return false;
			message = sendQueue.poll();
		}
		
		if(message == null)
			return false;
		
		return writeImmediately(message);
	}
	
	/**
	 * Read a single message from the socket's input stream, then forward it to any listening MessageTask.
	 * @return True if a message was read, false otherwise.
	 */
	private boolean readOne() {
		InputStream in = null;
		
		try {
			in = socket.getInputStream();
			
			HeaderParseResult headerResult = StreamUtils.readHeader(in);
			
			if(!headerResult.isSuccess()) {
				if(headerResult.getFailureArgIndex() == 0)
					return false;
				else {
					error = true; // Received an invalid header.
					return false;
				}
			} else {
				ByteBuffer buffer  = bufferPool.rent(headerResult.getValue().getContentSize());
				try {
					StreamUtils.read(buffer.array(), in, 0, headerResult.getValue().getContentSize());
					
					onMessage(headerResult.getValue(), buffer);
				} catch (Exception ex) {
					bufferPool.release(buffer);
					throw ex;
				}
				
				return true;
			}
		} catch (SocketTimeoutException ex) {}
		catch (IOException ex) {
			error = true;
		}
		
		return false;
	}
	
	/**
	 * Handle MessageTask events upon receiving a new message.
	 */
	private void onMessage(MessageHeader header, ByteBuffer content) {
		synchronized(handlers) {
			List<MessageHandler> toRemove = new ArrayList<>();
			for(MessageHandler handler : handlers) {
			
				if(handler.supports(header, content)) {
					handler.handle(header, content);
					if(handler.isComplete())
						handlers.remove(handler);
					break;
				} else if (handler.isComplete()) {
					toRemove.add(handler);
				}
			}
			
			for(MessageHandler handler : toRemove) {
				handlers.remove(handler);
			}
		}
		
		bufferPool.release(content);
	}
}
