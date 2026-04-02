package common.models.responses;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import common.MessageSerializer;
import common.MessageDefs;
import common.MessageDeserializer;
import common.MessageHeader;
import common.StreamUtils;
import common.models.UserSession;
import common.models.messages.Message;

public class LoginResponse extends Message<LoginResponse> {	
	private int sessionId;
	
	public LoginResponse(int sessionId) {
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
	
	public static LoginResponse from(MessageHeader header, ByteBuffer content) {
		MessageDeserializer msg = MessageDeserializer.fromHeader(header).setBytes(content.array());
		return new LoginResponse(msg.getIntegerAt(0));
	}
}
