package common.models;

import common.MessageBuilder;
import common.MessageDefs;

public class DownloadRequestMessage {
	private int count = -1;

	public DownloadRequestMessage(int count) {
		this.count = count;
	}
	
	public static MessageBuilder GetBuilder(DownloadRequestMessage message) {
		return new MessageBuilder()
				.setCode(MessageDefs.DOWNLOAD_MESSAGE)
				.appendContentInt(message.count);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
