package common;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class MessageHeader {
	
	private int code;
	private int[] sizes;
	private int crc;
	private int total;
	
	public MessageHeader(int code, int[] sizes, int crc) {
		this.code = code;
		this.sizes = sizes;
		this.crc = crc;
		
		for(int size : sizes) {
			total += size;
		}
	}
	
	public boolean validateCRC() {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * (sizes.length + 2));
		buffer.putInt(code);
		buffer.putInt(sizes.length);
		
		for(int i = 0; i < sizes.length; i++) {
			buffer.putInt(sizes[i]);
		}
		
		buffer.flip();
		CRC32 crc = new CRC32();
		crc.update(buffer);
		
		return ((int)crc.getValue()) == this.crc;
	}
	
	public int getCode() {
		return code;
	}
	
	public int[] getSizes() {
		return sizes;
	}
	
	public int getCRC() {
		return this.crc;
	}
	
	public int getSize() {
		return Integer.BYTES * (sizes.length + 3);
	}
	
	public int getContentSize() {
		return total;
	}
}