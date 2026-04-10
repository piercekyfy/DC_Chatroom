package common.models.messages;

import common.MessageSerializer;
import common.MessageTask;

public abstract class Message implements MessageBase {
	protected int code;
	
	public Message(int code) {
		this.code = code;
	}
	
	public abstract MessageSerializer serialize();
	
	public MessageTask asTask() {
		return new MessageTask(this);
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
