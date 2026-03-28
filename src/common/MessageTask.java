package common;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import common.models.AnyErrorMessage;
import common.models.ErrorMessage;
import common.models.Message;

public class MessageTask<T extends Message<T>> {
	private class MessageHandler<M extends Message<M>> {
		private final BiFunction<MessageHeader, ByteBuffer, M> deserializer;
		private final Consumer<M> callback;
		
		public MessageHandler(BiFunction<MessageHeader, ByteBuffer, M> deserializer, Consumer<M> callback) {
			this.deserializer = deserializer;
			this.callback = callback;
		}
		
		public void invoke(MessageHeader header, ByteBuffer content) {
			callback.accept(deserializer.apply(header, content));
		}
	}
	
	private int expectCode = MessageDefs.INVALID;
	private MessageHandler<T> expectHandler = null;
	private Map<Integer, MessageHandler<?>> errorHandlers = new HashMap<>();
	private Consumer<Exception> exceptionEvent = null;
	
	private Message<T> outgoing;
	
	public MessageTask(Message<T> outgoing) {
		this.outgoing = outgoing;
	}
	
	public boolean doesExpect(MessageHeader header, ByteBuffer content) {
		if(header.getCode() == expectCode)
			return true;
		else if (errorHandlers.containsKey(header.getCode())) {
			try {
				AnyErrorMessage error = AnyErrorMessage.from(header, content);
				if(expectCode == error.getSourceCode())
					return true;
			} catch (Exception e) {
				// fail quietly here (and hope it'll fail loudly if it is ever handled?)
			}
		}
		return false;
	}
	
	public void handleMessage(MessageHeader header, ByteBuffer content) {
		if(header.getCode() == expectCode)
			this.expectHandler.invoke(header, content);
		else {
			errorHandlers.get(header.getCode()).invoke(header, content);
		}
		// exception?
	}
	
	public MessageTask<T> expect(int code, BiFunction<MessageHeader, ByteBuffer, T> deserializer,  Consumer<T> callback) {
		expectCode = code;
		expectHandler = new MessageHandler<>(deserializer, callback);
		return this;
	}
	
	public <E extends ErrorMessage<E>> MessageTask<T> error(Integer code, BiFunction<MessageHeader, ByteBuffer, E> deserializer, Consumer<E> callback) {
		errorHandlers.put(code, new MessageHandler<E>(deserializer, callback));
		return this;
	}
	
	public MessageTask<T> error(Integer code, Consumer<AnyErrorMessage> callback) {
		errorHandlers.put(code, new MessageHandler<>(AnyErrorMessage::from, callback));
		return this;
	}
	
	public MessageTask<T> onException(Consumer<Exception> callback) {
		exceptionEvent = callback;
		return this;
	}
	
	public Message<T> getMessage() {
		return outgoing;
	}
}
