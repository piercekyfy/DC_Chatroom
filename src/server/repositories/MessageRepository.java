package server.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import common.models.TextMessage;
import common.models.UserSession;

public class MessageRepository {
	List<TextMessage> messages = Collections.synchronizedList(new ArrayList<TextMessage>());
	private int currentId = 0;
	
	private List<Consumer<TextMessage>> onMessageCreatedListeners = new ArrayList<>();
	
	public List<TextMessage> getAll() {
		synchronized (messages) {
			return new ArrayList<>(messages);
		}
	}
	
	public TextMessage getOne(int id) {
		synchronized (messages) {
			for(TextMessage message : messages) {
				if(message.getId() == id)
					return message;
			}
		}
		
		return null;
	}
	
	public TextMessage putOne(String username, String content) {
		synchronized (messages) {
			currentId += 1;
			TextMessage message = new TextMessage(currentId, username, content);
			messages.add(message);
			onMessageCreated(message);
			return message;
		}
	}
	
	public void registerOnMessageCreated(Consumer<TextMessage> callback) {
		onMessageCreatedListeners.add(callback);
	}
	
	private void onMessageCreated(TextMessage message) {
		for(Consumer<TextMessage> listener : onMessageCreatedListeners) {
			listener.accept(message);
		}
	}
}
