package common.models.requests;

import common.MessageSerializer;
import common.MessageDefs;
import common.models.UserSession;
import common.models.messages.Message;

public class DownloadMessageRequest extends Message {
	private int id;
	
	public DownloadMessageRequest(int id) {
		super(MessageDefs.DOWNLOAD_MESSAGE);
		this.id = id;
	}

	@Override
	public MessageSerializer serialize() {
		return new MessageSerializer()
				.setCode(code)
				.appendContentInt(id);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
