package common.models;

import java.io.InputStream;
import java.nio.ByteBuffer;

import common.MessageBuilder;
import common.MessageBus;
import common.MessageHeader;
import common.MessageTask;

public abstract class Message<T extends Message<T>>  {
	public abstract MessageBuilder serialize();
	public MessageTask<T> send(MessageBus bus) {
		MessageTask<T> task = new MessageTask<T>(this);
		bus.register(task);
		return task;
	}
}
