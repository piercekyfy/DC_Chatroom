package common.models.requests;

import common.MessageSerializer;
import common.MessageDefs;
import common.models.UserSession;
import common.models.messages.Message;

public class LoginRequest extends Message<LoginRequest> {	
	private String username;
	private String password;
	private int sessionId;
	
	public LoginRequest(String username, String password, int sessionId) {
		super(MessageDefs.LOGIN_REQUEST);
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
	}
	
	public LoginRequest(String username, String password) {
		super(MessageDefs.LOGIN_REQUEST);
		this.username = username;
		this.password = password;
		this.sessionId = UserSession.NO_SESSION_ID;
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentString(username)
				.appendContentString(password)
				.appendContentInt(sessionId);
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
}
