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

public class MessageContentResponse extends Message<MessageContentResponse> {	
	private int id;
	private String sender;
	private String content;
	
	public MessageContentResponse(int id, String sender, String content) {
		super(MessageDefs.MESSAGE_CONTENT);
		this.id = id;
		this.sender = sender;
		this.content = content;
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentInt(id)
				.appendContentString(sender)
				.appendContentString(content);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static MessageContentResponse from(MessageHeader header, ByteBuffer content) {
		MessageDeserializer msg = MessageDeserializer.fromHeader(header).setBytes(content.array());
		return new MessageContentResponse(msg.getIntegerAt(0), msg.getStringAt(1), msg.getStringAt(2));
	}
}
