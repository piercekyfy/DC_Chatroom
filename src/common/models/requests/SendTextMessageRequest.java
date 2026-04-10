package common.models.requests;

import common.MessageSerializer;
import common.MessageDefs;
import common.models.UserSession;
import common.models.messages.Message;

public class SendTextMessageRequest extends Message {
	private String content;
	
	public SendTextMessageRequest(String content) {
		super(MessageDefs.SEND_TEXT_MESSAGE);
		this.content = content;
	}

	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentString(content);
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
