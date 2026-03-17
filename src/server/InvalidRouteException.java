package server;

@SuppressWarnings("serial")
public class InvalidRouteException extends Exception {
	public InvalidRouteException(String errorMessage) {
		super(errorMessage);
	}
}
