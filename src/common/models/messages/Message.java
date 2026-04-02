package common.models.messages;

import common.MessageSerializer;
import common.MessageTask;

public abstract class Message<T extends Message<T>>  {
	protected int code;
	
	public Message(int code) {
		this.code = code;
	}
	
	public abstract MessageSerializer serialize();
	
	public MessageTask<T> asTask() {
		return new MessageTask<T>(this);
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
