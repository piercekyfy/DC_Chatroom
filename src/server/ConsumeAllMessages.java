package server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import common.MessageHandler;
import common.MessageTask;
import common.models.messages.InvalidMessage;
import common.models.messages.MessageHeader;

public class ConsumeAllMessages implements MessageHandler {
	
	private List<BiConsumer<MessageHeader, ByteBuffer>> consumers = new ArrayList<>();
	private boolean completed = false;

	@Override
	public boolean supports(MessageHeader header, ByteBuffer content) {
		return true;
	}

	@Override
	public void handle(MessageHeader header, ByteBuffer content) {
		for(BiConsumer<MessageHeader, ByteBuffer> consumer : consumers) {
			consumer.accept(header, content);
		}
	}
	
	@Override
	public void handleStopped() {
		return; // no-op
	}

	@Override
	public boolean isComplete() {
		return completed;
	}
	
	public void register(BiConsumer<MessageHeader, ByteBuffer> consumer) {
		consumers.add(consumer);
	}
	
	public void complete() {
		completed = true;
	}
}
