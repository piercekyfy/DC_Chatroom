package common.models;

import java.time.LocalDateTime;

import common.MessageBuilder;
import common.MessageDefs;
import common.StreamUtils;

public class TextMessage {
	private int code = MessageDefs.INVALID;
	private int senderId = -1;
	private LocalDateTime timestamp;
	private String content = "";
	
	public TextMessage(int code, int senderId, LocalDateTime timestamp, String content) {
		this.code = code;
		this.senderId = senderId;
		this.timestamp = timestamp;
		this.content = content;
	}
	
	public static MessageBuilder GetBuilder(TextMessage message) {
		return new MessageBuilder()
				.setCode(MessageDefs.NEW_MESSAGE)
				.appendContentInt(message.getSenderId())
				.appendContentString(StreamUtils.formatDatetime(message.getTimestamp()))
				.appendContentString(message.getContent());
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
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
}
