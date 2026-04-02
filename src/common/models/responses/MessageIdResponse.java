package common.models.responses;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.MessageSerializer;
import common.MessageDefs;
import common.MessageDeserializer;
import common.MessageHeader;
import common.StreamUtils;
import common.models.UserSession;
import common.models.messages.Message;

public class MessageIdResponse extends Message<MessageIdResponse> {	
	private int id;
	
	public MessageIdResponse(int id) {
		super(MessageDefs.MESSAGE_ID);
		this.id = id;
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentInt(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static MessageIdResponse from(MessageHeader header, ByteBuffer content) {
		MessageDeserializer msg = MessageDeserializer.fromHeader(header).setBytes(content.array());
		return new MessageIdResponse(msg.getIntegerAt(0));
	}
}
