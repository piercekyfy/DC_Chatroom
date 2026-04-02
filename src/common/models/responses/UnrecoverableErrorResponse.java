package common.models.responses;

import java.nio.ByteBuffer;

import common.MessageSerializer;
import common.models.messages.ErrorMessage;
import common.MessageDefs;
import common.MessageHeader;

public class UnrecoverableErrorResponse extends ErrorMessage<UnrecoverableErrorResponse>  {

	public UnrecoverableErrorResponse(int subCode, int sourceCode) {
		super(MessageDefs.UNRECOVERABLE_ERROR, subCode, sourceCode);
	}

	public static UnrecoverableErrorResponse from(MessageHeader header, ByteBuffer content) {
		return new UnrecoverableErrorResponse(content.getInt(), content.getInt());
	}
}

