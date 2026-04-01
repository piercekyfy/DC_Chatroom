package common.models.messages;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.MessageSerializer;
import common.MessageDefs;
import common.MessageDeserializer;
import common.MessageHeader;
import common.StreamUtils;
import common.models.UserSession;

public class LoginResponseMessage extends Message<LoginResponseMessage> {	
	private int sessionId;
	
	public LoginResponseMessage(int sessionId) {
		super(MessageDefs.LOGIN_RESPONSE);
		this.sessionId = sessionId;
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentInt(sessionId);
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	public static LoginResponseMessage from(MessageHeader header, ByteBuffer content) {
		MessageDeserializer msg = MessageDeserializer.fromHeader(header).setBytes(content.array());
		return new LoginResponseMessage(msg.getIntegerAt(0));
	}
}
