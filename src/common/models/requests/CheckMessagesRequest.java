package common.models.requests;

import common.MessageSerializer;
import common.MessageDefs;
import common.models.UserSession;
import common.models.messages.Message;

public class CheckMessagesRequest extends Message<CheckMessagesRequest> {	
	
	public CheckMessagesRequest() {
		super(MessageDefs.CHECK_MESSAGES);
	}
	
	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code);
	}
}
