package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import common.models.TextMessage;

public class MessageRepository {
	List<TextMessage> messages = Collections.synchronizedList(new ArrayList<TextMessage>());
	
	public List<TextMessage> getAll() {
		synchronized (messages) {
			return new ArrayList<>(messages);
		}
	}
	
	public void putOne(TextMessage message) {
		synchronized (messages) {
			messages.add(message);
		}
	}
}
