package common;

public abstract class Message {
	protected MessageHeader header;
	
	public Message(MessageHeader header) {
		this.header = header;
	}
	
	public MessageHeader getHeader() {
		return header;
	}
	public abstract byte[] getContent();
}
