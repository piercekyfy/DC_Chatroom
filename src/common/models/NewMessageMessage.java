package common.models;

import java.time.LocalDateTime;

import common.MessageBuilder;
import common.MessageDefs;
import common.StreamUtils;

public class NewMessageMessage {
	private int code = MessageDefs.INVALID;
	private int senderId = -1;
	private LocalDateTime timestamp;
	private int fromRequest = 0;
	private String content = "";
	
	public NewMessageMessage(int code, int senderId, LocalDateTime timestamp, boolean fromRequest, String content) {
		this.code = code;
		this.senderId = senderId;
		this.timestamp = timestamp;
		this.fromRequest = fromRequest ? 1 : 0;
		this.content = content;
	}
	
	public static MessageBuilder GetBuilder(NewMessageMessage message) {
		return new MessageBuilder()
				.setCode(MessageDefs.NEW_MESSAGE)
				.appendContentInt(message.getSenderId())
				.appendContentString(StreamUtils.formatDatetime(message.getTimestamp()))
				.appendContentInt(message.getFromRequest())
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
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public int getFromRequest() {
		return fromRequest;
	}

	public void setFromRequest(int fromRequest) {
		this.fromRequest = fromRequest;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
