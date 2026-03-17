package server;

@SuppressWarnings("serial")
public class NotFoundException extends Exception {
	public NotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
