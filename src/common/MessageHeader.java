package common;

public class MessageHeader {
	protected MessageCode code;
	protected int payloadSize;
	
	public MessageHeader(MessageCode code, int payloadSize) {
		this.code = code;
		this.payloadSize = payloadSize;
	}
	
	public MessageCode getCode() {
		return code;
	}
	public int getPayloadSize() {
		return payloadSize;
	}
	public void setPayloadSize(int size) {
		payloadSize = size;
	}
}