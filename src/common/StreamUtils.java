package common;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StreamUtils {
	
	public static HeaderParseResult readHeader(InputStream in) throws IOException { 
		byte[] headerBuffer = new byte[Integer.BYTES];
		
		ParseResult<Integer> codeResult = StreamUtils.readInt(headerBuffer, in, 0);
		
		if(!codeResult.isSuccess()) {
			return new HeaderParseResult(false, null, 0);
		}
		
		ParseResult<Integer> countResult = StreamUtils.readInt(headerBuffer, in, 0);
		
		if(!countResult.isSuccess()) {
			return new HeaderParseResult(false, null, 1);
		}
		
		int[] sizes = new int[countResult.getValue()];
		
		for(int i = 0; i < countResult.getValue(); i++) {
			ParseResult<Integer> sizeResult = StreamUtils.readInt(headerBuffer, in, 0);
			
			if(!sizeResult.isSuccess()) {
				return new HeaderParseResult(false, null, 2);
			}
			
			sizes[i] = sizeResult.getValue();
		}
		
		ParseResult<Integer> crcResult = StreamUtils.readInt(headerBuffer, in, 0);
		
		if(!crcResult.isSuccess()) {
			return new HeaderParseResult(false, null, 3);
		}
		
		MessageHeader header = new MessageHeader(codeResult.getValue(), sizes, crcResult.getValue());
		
		if(!header.validateCRC()) {
			return new HeaderParseResult(false, header, 4);
		}
		
		return new HeaderParseResult(true, header);
	}
	
	public static ParseResult<Integer> readInt(byte[] buffer, InputStream in, int offset) throws IOException {
		boolean success = StreamUtils.read(buffer, in, offset, Integer.BYTES);
		
		if(!success)
			return new ParseResult<Integer>(false, 0);
		
		return parseInt(buffer, offset);
	}
	
	// TODO: refactor this to just 'parse int from buffer'
	public static ParseResult<Integer> parseInt(byte[] buffer, int offset) {
		try {
			int value = ByteBuffer.wrap(buffer, offset, Integer.BYTES).getInt();
			return new ParseResult<Integer>(true, value);
		} catch (BufferUnderflowException ex) {
			return new ParseResult<Integer>(false, 0);
		}
	}
	
	public static ParseResult<String> parseString(byte[] buffer, int offset, int length) {
		try {
			String value = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buffer, offset, length)).toString();
			return new ParseResult<String>(true, value);
		} catch (BufferUnderflowException ex) {
			return new ParseResult<String>(false, null);
		}
	}
	
	public static boolean read(byte[] buffer, InputStream in, int offset, int length) throws IOException {
		int read = 0;
		
		try {
			while(read < length) {
				int lastRead = in.read(buffer, offset + read, length - read);
				
				if(lastRead == -1)
					throw new IOException("Stream was unexpectedly closed.");
				
				read += lastRead;
			}
		} catch (SocketTimeoutException ex) {
			return false;
		}
		
		return true;
	}
	
	public static String formatDatetime(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
	
	public static LocalDateTime dateTimeFromString(String str) {
		return LocalDateTime.parse(str.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
