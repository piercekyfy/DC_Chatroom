package common;

import common.models.messages.MessageHeader;

public class HeaderParseResult extends ParseResult<MessageHeader> {
	private int failureArgIndex = -1;
	
	public HeaderParseResult(boolean success, MessageHeader value) {
		super(success, value);
	}
	
	public HeaderParseResult(boolean success, MessageHeader value, int failureArgIndex) {
		super(success, value);
		this.failureArgIndex = failureArgIndex;
	}
	
	public int getFailureArgIndex() {
		return failureArgIndex;
	}
}
