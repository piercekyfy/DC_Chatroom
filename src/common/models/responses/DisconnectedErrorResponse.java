package common.models.responses;

import java.nio.ByteBuffer;

import common.MessageSerializer;
import common.models.messages.ErrorMessage;
import common.MessageDefs;
import common.MessageHeader;

public class DisconnectedErrorResponse extends ErrorMessage<DisconnectedErrorResponse>  {

	public DisconnectedErrorResponse(int subCode, int sourceCode) {
		super(MessageDefs.DISCONNECTED_ERROR, subCode, sourceCode);
	}

	public static DisconnectedErrorResponse from(MessageHeader header, ByteBuffer content) {
		return new DisconnectedErrorResponse(content.getInt(), content.getInt());
	}
}

