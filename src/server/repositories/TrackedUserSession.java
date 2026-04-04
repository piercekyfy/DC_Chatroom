package server.repositories;

import java.time.LocalDateTime;

import common.models.UserSession;

public class TrackedUserSession extends UserSession {

	private UserRepository repository;
	
	public TrackedUserSession(UserRepository repository, String username, int sessionId) {
		super(username, sessionId);
		this.repository = repository;
	}

	@Override
	public void setOnline(boolean online) {
		super.setOnline(online);
		repository.onSessionUpdated(this);
	}
	
}
