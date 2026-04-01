package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import common.models.UserSession;
import common.models.messages.TextMessage;

public class UserRepository {
	List<UserSession> sessions = Collections.synchronizedList(new ArrayList<UserSession>());
	private int currentId = 0;
	
	private List<Consumer<UserSession>> onSessionCreatedListeners = new ArrayList<>();
	private List<Consumer<UserSession>> onSessionRemovedListeners = new ArrayList<>();
	
	public List<UserSession> getAll() {
		synchronized (sessions) {
			return new ArrayList<>(sessions);
		}
	}
	
	public UserSession getOne(int sessionId) {
		synchronized (sessions) {
			for(UserSession session : sessions) {
				if(session.getSessionId() == sessionId)
					return session;
			}
		}
		return null;
	}
	
	public UserSession putOne(String username) {
		synchronized (sessions) {
			for(UserSession session : sessions) {
				if(session.getUsername() == username) {
					sessions.remove(session);
					break;
				}
			}
			
			currentId += 1;
			UserSession session = new UserSession(username, currentId);
			sessions.add(session);
			onSessionCreated(session);
			return session;
		}
	}
	
	public void registerOnSessionCreated(Consumer<UserSession> callback) {
		onSessionCreatedListeners.add(callback);
	}
	
	public void registerOnSessionRemoved(Consumer<UserSession> callback) {
		onSessionRemovedListeners.add(callback);
	}
	
	private void onSessionCreated(UserSession session) {
		for(Consumer<UserSession> listener : onSessionCreatedListeners) {
			listener.accept(session);
		}
	}
	
	private void onSessionRemoved(UserSession session) {
		for(Consumer<UserSession> listener : onSessionRemovedListeners) {
			listener.accept(session);
		}
	}
}
