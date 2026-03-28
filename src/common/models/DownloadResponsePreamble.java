package common.models;

import common.MessageBuilder;
import common.MessageDefs;

public class DownloadResponsePreamble {
	private int count = -1;

	public DownloadResponsePreamble(int count) {
		this.count = count;
	}
	
	public static MessageBuilder GetBuilder(DownloadResponsePreamble message) {
		return new MessageBuilder()
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
