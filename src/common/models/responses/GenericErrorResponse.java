package common.models.responses;

import java.nio.ByteBuffer;

import common.MessageSerializer;
import common.models.messages.ErrorMessage;
import common.models.messages.MessageHeader;
import common.MessageDefs;

public class GenericErrorResponse extends ErrorMessage  {

	public GenericErrorResponse(int subCode, int sourceCode) {
		super(MessageDefs.GENERIC_ERROR, subCode, sourceCode);
	}

	public static GenericErrorResponse from(MessageHeader header, ByteBuffer content) {
		return new GenericErrorResponse(content.getInt(), content.getInt());
	}
}

