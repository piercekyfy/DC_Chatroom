package common.models.messages;

import java.io.InputStream;
import java.nio.ByteBuffer;

import common.MessageBuilder;
import common.MessageBus;
import common.MessageHeader;
import common.MessageTask;

public abstract class Message<T extends Message<T>>  {
	protected int code;
	
	public abstract MessageBuilder serialize();
	public MessageTask<T> send(MessageBus bus) {
		MessageTask<T> task = new MessageTask<T>(this);
		bus.register(task);
		return task;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
}
