package server.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import common.models.UserSession;

public class UserRepository {
	List<TrackedUserSession> sessions = Collections.synchronizedList(new ArrayList<TrackedUserSession>()); // Could be a hashmap
	private int currentId = 0;
	
	private List<Consumer<UserSession>> onSessionCreatedListeners = new ArrayList<>();
	private List<Consumer<UserSession>> onSessionUpdatedListeners = new ArrayList<>();
	private List<Consumer<List<UserSession>>> onSessionRemovedListeners = new ArrayList<>();
	
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
			TrackedUserSession session = new TrackedUserSession(this, username, currentId);
			sessions.add(session);
			onSessionCreated(session);
			return session;
		}
	}
	
	public void removeSessions(String username) {
		synchronized (sessions) {
			List<UserSession> toRemove = new ArrayList<UserSession>();
			
			for(UserSession session : sessions) {
				if(session.getUsername() == username) {
					toRemove.add(session);
				}
			}
			
			for(UserSession s : toRemove) {
				sessions.remove(s);
			}
			
			onSessionRemoved(toRemove);
		}
	}
	
	public void registerOnSessionCreated(Consumer<UserSession> callback) {
		onSessionCreatedListeners.add(callback);
	}
	
	public void registerOnSessionUpdated(Consumer<UserSession> callback) {
		onSessionUpdatedListeners.add(callback);
	}
	
	public void registerOnSessionRemoved(Consumer<List<UserSession>> callback) {
		onSessionRemovedListeners.add(callback);
	}
	
	private void onSessionCreated(UserSession session) {
		for(Consumer<UserSession> listener : onSessionCreatedListeners) {
			listener.accept(session);
		}
	}
	
	public void onSessionUpdated(UserSession session) {
		for(Consumer<UserSession> listener : onSessionUpdatedListeners) {
			listener.accept(session);
		}
	}
	
	private void onSessionRemoved(List<UserSession> sessions) {
		for(Consumer<List<UserSession>> listener : onSessionRemovedListeners) {
			listener.accept(sessions);
		}
	}
}
