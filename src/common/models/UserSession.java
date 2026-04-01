package common.models;

import java.time.LocalDateTime;

public class UserSession {
	

	public static final int NO_SESSION_ID = -1;
	public static final int TIMEOUT_MINUTES = 5;
	
	private String username = null;
	private int sessionId = UserSession.NO_SESSION_ID;
	private LocalDateTime lastUpdated;
	
	public UserSession(String username, int sessionId) {
		this.username = username;
		this.sessionId = sessionId;
		this.lastUpdated = LocalDateTime.now(); // TODO: utc
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public void setUpdated() {
		this.lastUpdated = LocalDateTime.now(); // TODO: utc
	}
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(lastUpdated.plusMinutes(5));
	}
}
