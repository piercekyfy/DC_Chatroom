package common;

public class MessageDefs {
	// /-- Error Responses
	
	public static final int INVALID = 0;
	public static final int GENERIC_ERROR = 1;
	public static final int UNRECOVERABLE_ERROR = 2;
	public static final int DISCONNECTED_ERROR = 3;
			
	// --/
	
	// /-- User Session and Account
	
	public static final int LOGIN_REQUEST = 20;
	public static final int LOGIN_RESPONSE = 21;
	public static final int LOGOUT_REQUEST = 22;
	
	// --/
	
	// /-- Messages
	
	public static final int CHECK_MESSAGES = 30;
	public static final int MESSAGE_ID = 31;
	public static final int DOWNLOAD_MESSAGE = 32;
	public static final int MESSAGE_CONTENT = 33;
	public static final int SEND_TEXT_MESSAGE = 34;
	
	// --/
}