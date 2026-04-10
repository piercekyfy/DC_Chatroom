package common;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import common.models.messages.ErrorMessage;
import common.models.messages.Message;
import common.models.messages.MessageBase;
import common.models.messages.MessageHeader;
import common.models.responses.GenericErrorResponse;

public class MessageTask implements MessageHandler {
	private class MessageConsumer<M extends Message> {
		private BiFunction<MessageHeader, ByteBuffer, M> deserializer;
		private Consumer<M> callback;
		
		public MessageConsumer(BiFunction<MessageHeader, ByteBuffer, M> deserializer, Consumer<M> callback) {
			this.deserializer = deserializer;
			this.callback = callback;
		}
		
		public void invoke(MessageHeader header, ByteBuffer content) {
			callback.accept(deserializer.apply(header, content));
		}
	}
	
	private int expectCode = MessageDefs.INVALID;
	private MessageConsumer<?> expectHandler = null;
	private Map<Integer, MessageConsumer<?>> errorHandlers = new HashMap<>();
	private Runnable closedHandler = null;
	
	private boolean completed = true;
	
	private MessageBase outgoing;
	
	public MessageTask(MessageBase outgoing) {
		this.outgoing = outgoing;
	}
	
	public void send(MessageBus bus) {
		bus.register(this);
	}
	
	public boolean expectsAny() {
		return expectHandler != null || errorHandlers.size() > 0 || closedHandler != null;
	}
	
	public boolean supports(MessageHeader header, ByteBuffer content) {
		if(header.getCode() == expectCode)
			return true;
		else if (errorHandlers.containsKey(header.getCode())) {
			try {
				GenericErrorResponse error = GenericErrorResponse.from(header, content);
				if(outgoing.getCode()  == error.getSourceCode())
					return true;
			} catch (Exception e) {
				// fail quietly here (and hope it'll fail loudly if it is ever handled?)
			}
		}
		return false;
	}
	
	public void handle(MessageHeader header, ByteBuffer content) {
		if(header.getCode() == expectCode)
			this.expectHandler.invoke(header, content);
		else {
			errorHandlers.get(header.getCode()).invoke(header, content);
		}
	}
	
	public void handleStopped() {
		if(closedHandler != null)
			closedHandler.run();
	}
	
	public boolean isComplete() {
		return this.completed;
	}
	
	public void complete() {
		this.completed = true;
	}
	
	public <M extends Message> MessageTask expect(int code, BiFunction<MessageHeader, ByteBuffer, M> deserializer,  Consumer<M> callback) {
		expectCode = code;
		expectHandler = new MessageConsumer<M>(deserializer, callback);
		return this;
	}
	
	public <E extends ErrorMessage> MessageTask error(int code, BiFunction<MessageHeader, ByteBuffer, E> deserializer, Consumer<E> callback) {
		errorHandlers.put(code, new MessageConsumer<E>(deserializer, callback));
		return this;
	}
	
	public MessageTask error(Integer code, Consumer<GenericErrorResponse> callback) {
		errorHandlers.put(code, new MessageConsumer<>(GenericErrorResponse::from, callback));
		return this;
	}
	
	public MessageTask closed(Runnable callback) {
		closedHandler = callback;
		return this;
	}
	
	public MessageTask dontExpireOnComplete() {
		this.completed = false;
		return this;
	}
	
	public Message getMessage() {
		return (Message) outgoing;
	}
}
