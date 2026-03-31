package client;

public class UserContext {
	public String username;	
	public int sessionId;
	
	public UserContext(String username, int sessionId) {
		this.username = username;
		this.sessionId = sessionId;
	}
	
	public String getUsername() {
		return username;
	}
	public int getSessionId() {
		return sessionId;
	}
}
