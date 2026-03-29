package common.models.messages;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.MessageBuilder;
import common.MessageDefs;
import common.MessageHeader;
import common.StreamUtils;

public class TextMessage extends Message<TextMessage> {
	private int senderId = -1;
	private LocalDateTime timestamp;
	private String content = "";
	
	public TextMessage(int code, int senderId, LocalDateTime timestamp, String content) {
		this.code = code;
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
	public MessageBuilder serialize() {
		return new MessageBuilder()
				.setCode(code)
				.appendContentInt(senderId)
				.appendContentString(StreamUtils.formatDatetime(timestamp))
				.appendContentString(content);
	}
	
	public static TextMessage from(MessageHeader header, ByteBuffer content) {
		
		int code = header.getCode();
		int sender = content.getInt(); // size 0
		byte[] timestampBytes = new byte[header.getSizes()[1]];
		byte[] contentBytes = new byte[header.getSizes()[2]];
		content.get(timestampBytes);
		content.get(contentBytes);
		LocalDateTime timestamp = StreamUtils.dateTimeFromString(StreamUtils.parseString(timestampBytes, 0, timestampBytes.length).getValue());
		String text = StreamUtils.parseString(contentBytes, 0, contentBytes.length).getValue();
		
		return new TextMessage(code, sender, timestamp, text);
	}
}
