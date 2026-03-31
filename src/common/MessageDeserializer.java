package common;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDeserializer {
	private List<Integer> sizes = new ArrayList<Integer>();
	private byte[] bytes = null;
	
	public void reset() {
		sizes = new ArrayList<Integer>();
		bytes = null;
	}
	
	public MessageDeserializer setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}
	
	public int getIntegerAt(int index) {
		if(bytes == null)
			return -1; // TODO: don't be lazy and use a real throw
		
		return StreamUtils.parseInt(bytes, getOffsetTo(index)).getValue();
	}
	
	public boolean getBooleanAt(int index) {
		if(bytes == null)
			return false;
		
		return (getIntegerAt(index) >= 1);
	}
	
	public String getStringAt(int index) {
		if(bytes == null)
			return null;
		
		return StreamUtils.parseString(bytes, getOffsetTo(index), sizes.get(index)).getValue();
	}
	
	public LocalDateTime getDateTimeAt(int index) {
		return StreamUtils.dateTimeFromString(getStringAt(index));
	}
	
	private int getOffsetTo(int index) {
		int offset = 0;
		for(int size : sizes) {
			offset += size;
		}
		return offset;
	}
	
	public static MessageDeserializer fromHeader(MessageHeader header) {
		MessageDeserializer d = new MessageDeserializer();
		
		for(int size : header.getSizes()) {
			d.sizes.add(size);
		}
		
		return d;
	}
}
