package common;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// A simple pool with no capacity limits. 
// It is very rare for client or server systems to ever rent more than two buffers.
public class ByteBufferPool {
	private List<ByteBuffer> available = new ArrayList<ByteBuffer>();
	private List<ByteBuffer> rented = new ArrayList<ByteBuffer>();
	
	public ByteBuffer rent(int size) {
		for(ByteBuffer buffer : available) {
			if(buffer.capacity() >= size) {
				available.remove(buffer);
				rented.add(buffer);
				return buffer;
			}
		}
		
		ByteBuffer newBuffer = ByteBuffer.allocate(size);
		rented.add(newBuffer);
		return newBuffer;
	}
	
	public void release(ByteBuffer rentedBuffer) {
		boolean valid = false;
		for(ByteBuffer buffer : rented) {
			if(buffer == rentedBuffer)
				valid = true;
		}
		
		if(!valid)
			throw new IllegalArgumentException("Attempted to return a buffer to pool which it does not belong to.");
		
		rentedBuffer.clear();
		rented.remove(rentedBuffer);
		available.add(rentedBuffer);
	}
}
