package common;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageContentElement {
	private Integer intValue = null;
	private String stringValue = null;
	
	private byte[] bytes = null;
	
	public MessageContentElement(int value) {
		intValue = value;
	}
	
	public MessageContentElement(String value) {
		stringValue = value;
	}
	
	public int getSize() {
		return getBytes().length;
	}
	
	public byte[] getBytes() {
		if(intValue != null) {
			bytes = ByteBuffer.allocate(Integer.BYTES).putInt(intValue).array();
			return bytes;
		} else if (stringValue != null) {
			bytes = StandardCharsets.UTF_8.encode(stringValue).array();
			return bytes;
		}
		
		return null;
	}
}
