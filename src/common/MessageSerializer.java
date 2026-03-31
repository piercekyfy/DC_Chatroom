package common;

import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import common.models.messages.TextMessage;

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
		
		buffer.flip();
		CRC32 crc = new CRC32();
		crc.update(buffer);

		return new MessageHeader(code, sizes, (int)crc.getValue());
	}
	
	public byte[] build() {
		MessageHeader header = buildHeader();
		
		ByteBuffer buffer = ByteBuffer.allocate(header.getSize() + header.getContentSize());
		
		buffer.putInt(header.getCode());
		buffer.putInt(header.getSizes().length);
		for(int size : header.getSizes())
			buffer.putInt(size);
		buffer.putInt(header.getCRC());
		
		for(MessageContentElement element : content) {
			buffer.put(element.getBytes());
		}
		
		return buffer.array();
	}
	
	// Pre-defined messages
	
	public MessageSerializer setAsInvalidHeaderArg(int argIndex) {
		reset();
		setCode(MessageDefs.INVALID_HEADER_ERROR);
		appendContentInt(ErrorDefs.INVALID_OR_MISSING_ARG);
		appendContentInt(argIndex);
		return this;
	}
	
	public MessageSerializer setAsInvalidContent(int index) {
		reset();
		setCode(MessageDefs.INVALID_CONTENT_ERROR);
		appendContentInt(index);
		return this;
	}
}
