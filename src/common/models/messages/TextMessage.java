package common.models.messages;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.MessageSerializer;
import common.MessageDefs;
import common.MessageDeserializer;
import common.MessageHeader;
import common.StreamUtils;

public class TextMessage extends Message<TextMessage> {
	private int senderId = -1;
	private LocalDateTime timestamp;
	private String content = "";
	
	public TextMessage(int code, int senderId, LocalDateTime timestamp, String content) {
		super(code);
		this.senderId = senderId;
		this.timestamp = timestamp;
		this.content = content;
	}
	
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentInt(senderId)
				.appendContentString(StreamUtils.formatDatetime(timestamp))
				.appendContentString(content);
	}
	
	public static TextMessage from(MessageHeader header, ByteBuffer content) {
		MessageDeserializer msg = MessageDeserializer.fromHeader(header).setBytes(content.array());
		return new TextMessage(header.getCode(), msg.getIntegerAt(0), msg.getDateTimeAt(1), msg.getStringAt(2));
	}
}
