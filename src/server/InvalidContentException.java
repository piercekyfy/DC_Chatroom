package server;

@SuppressWarnings("serial")
public class InvalidContentException extends Exception {
	public InvalidContentException(String errorMessage) {
		super(errorMessage);
	}
}
