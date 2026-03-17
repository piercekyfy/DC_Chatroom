package common;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class OperationResponseMessage extends Message {
	private int size;
	private byte[] content;
	
	public OperationResponseMessage(boolean success, String message) {
		super(new MessageHeader(MessageCode.OP_RESPONSE, 0));
		int successInt = success ? 1 : 0;
		if(message != null) {
			byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
			content = ByteBuffer.allocate((Integer.SIZE / 8) + messageBytes.length)
					.putInt(successInt)
					.put(messageBytes)
					.array();
			header.setPayloadSize(content.length);
		} else {
			content = ByteBuffer.allocate(Integer.SIZE / 8)
					.putInt(successInt)
					.array();
			header.setPayloadSize(content.length);
		}
	}
	
	public OperationResponseMessage(boolean success) {
		this(true, null);
	}
	
	public OperationResponseMessage(MessageHeader header, byte[] bytes) {
		super(new MessageHeader(MessageCode.OP_RESPONSE, header.getPayloadSize()));
		
	}

	@Override
	public byte[] getContent() {
		return content;
	}

	@Override
	public byte[] fromBytes() {
		content = ByteBuffer.
	}
}
