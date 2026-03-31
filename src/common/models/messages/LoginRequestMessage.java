package common.models.messages;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.LoginSession;
import common.MessageSerializer;
import common.MessageDefs;
import common.MessageHeader;
import common.StreamUtils;

public class LoginRequestMessage extends Message<LoginRequestMessage> {	
	private String username;
	private String password;
	private int sessionId;
	
	public LoginRequestMessage(String username, String password, int sessionId) {
		super(MessageDefs.LOGIN_REQUEST);
		this.username = username;
		this.password = password;
		this.sessionId = sessionId;
	}
	
	public LoginRequestMessage(String username, String password) {
		super(MessageDefs.LOGIN_REQUEST);
		this.username = username;
		this.password = password;
		this.sessionId = LoginSession.NO_SESSION_ID;
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
