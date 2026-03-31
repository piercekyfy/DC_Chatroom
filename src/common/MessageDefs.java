package common;

public class MessageDefs {
	public static final int INVALID = 0;
	public static final int BROADCAST = 1;
	public static final int RESPONSE_SUCCESS = 200;
	public static final int INVALID_HEADER_ERROR = 400;
	public static final int INVALID_CONTENT_ERROR = 401;
	public static final int DOWNLOAD_MESSAGE = 801;
	public static final int DOWNLOAD_MESSAGE_RESPONSE_PREAMBLE = 898;
	public static final int NEW_MESSAGE = 899;
	public static final int GENERIC_ERROR = 999;
	
	// Login
	public static final int LOGIN_REQUEST = 20;
	public static final int LOGIN_RESPONSE = 21;
	public static final int INVALID_LOGIN_ERROR = 22;
}
