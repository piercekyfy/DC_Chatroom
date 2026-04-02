package common;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class MessageHeader {
	
	private int code;
	private int[] sizes;
	private int total;
	
	public MessageHeader(int code, int[] sizes) {
		this.code = code;
		this.sizes = sizes;

		for(int size : sizes) {
			total += size;
		}
	}
	
	public int getCode() {
		return code;
	}
	
	public int[] getSizes() {
		return sizes;
	}
	
	public int getSize() {
		return Integer.BYTES * (sizes.length + 2);
	}
	
	public int getContentSize() {
		return total;
	}
}