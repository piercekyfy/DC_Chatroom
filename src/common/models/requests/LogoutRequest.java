package common.models.requests;

import common.MessageSerializer;
import common.MessageDefs;
import common.models.UserSession;
import common.models.messages.Message;

public class LogoutRequest extends Message {	
	
	public LogoutRequest() {
		super(MessageDefs.LOGOUT_REQUEST);
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code);
	}
}
