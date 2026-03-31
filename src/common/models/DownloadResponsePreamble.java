package common.models;

import common.MessageSerializer;
import common.MessageDefs;

public class DownloadResponsePreamble {
	private int count = -1;

	public DownloadResponsePreamble(int count) {
		this.count = count;
	}
	
	public static MessageSerializer GetBuilder(DownloadResponsePreamble message) {
		return new MessageSerializer()
				.setCode(MessageDefs.DOWNLOAD_MESSAGE_RESPONSE_PREAMBLE)
				.appendContentInt(message.count);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
