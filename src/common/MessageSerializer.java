package common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MessageSerializer {
	private int code = MessageDefs.INVALID;
	private List<MessageContentElement> content = new ArrayList<MessageContentElement>();
	
	public void reset() {
		code = MessageDefs.INVALID;
		content = new ArrayList<MessageContentElement>();
	}
	
	public MessageSerializer setCode(int code) {
		this.code = code;
		return this;
	}
	
	public MessageSerializer appendContentInt(int value) {
		content.add(new MessageContentElement(value));
		return this;
	}
	
	public MessageSerializer setContentInt(int index, int value) {
		content.set(index, new MessageContentElement(value));
		return this;
	}
	
	public MessageSerializer appendContentString(String value) {
		content.add(new MessageContentElement(value));
		return this;
	}
	
	public MessageSerializer setContentString(int index, String value) {
		content.set(index, new MessageContentElement(value));
		return this;
	}

	public MessageHeader buildHeader() {
		int sizes[] = new int[content.size()];
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * (sizes.length + 2));
		buffer.putInt(code);
		buffer.putInt(sizes.length);
		
		for(int i = 0; i < sizes.length; i++) {
			sizes[i] = content.get(i).getSize();
			buffer.putInt(sizes[i]);
		}

		return new MessageHeader(code, sizes);
	}
	
	public byte[] build() {
		MessageHeader header = buildHeader();
		
		ByteBuffer buffer = ByteBuffer.allocate(header.getSize() + header.getContentSize());
		
		buffer.putInt(header.getCode());
		buffer.putInt(header.getSizes().length);
		for(int size : header.getSizes())
			buffer.putInt(size);
		
		for(MessageContentElement element : content) {
			buffer.put(element.getBytes());
		}
		
		return buffer.array();
	}
}
