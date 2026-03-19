package server;

@SuppressWarnings("serial")
public class InvalidContentException extends Exception {
	private int index;
	
	public InvalidContentException(int index) {
		super("");
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
