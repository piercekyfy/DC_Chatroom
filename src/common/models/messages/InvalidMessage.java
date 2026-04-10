package common.models.messages;

import common.MessageDefs;
import common.MessageSerializer;

public class InvalidMessage extends Message {

	public InvalidMessage() {
		super(MessageDefs.INVALID);
	}

	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer().setCode(this.code);
	}

}
