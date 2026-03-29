package common.models.messages;

import java.nio.ByteBuffer;

import common.MessageBuilder;
import common.MessageHeader;

public class AnyErrorMessage extends ErrorMessage<AnyErrorMessage>  {
	/**
	 * 
	 * @param code The error code itself, e.g. INVALID_HEADER_ERROR (400).
	 * @param subCode A further specifying sub-code contained in the error's content, e.g. INVALID_OR_MISSING_ARG (1).
	 * @param sourceCode The original request's code, e.g. BROADCAST (1).
	 */
	public AnyErrorMessage(int code, int subCode, int sourceCode) {
		super(code, subCode, sourceCode);
	}

	public static AnyErrorMessage from(MessageHeader header, ByteBuffer content) {
		return new AnyErrorMessage(header.getCode(), content.getInt(), content.getInt());
	}
}

