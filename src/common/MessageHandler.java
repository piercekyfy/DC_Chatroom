package common;

import java.nio.ByteBuffer;

import common.models.messages.MessageHeader;

public interface MessageHandler {
	
	public boolean supports(MessageHeader header, ByteBuffer content);
	public void handle(MessageHeader header, ByteBuffer content);
	public void handleStopped();
	public boolean isComplete();
}
