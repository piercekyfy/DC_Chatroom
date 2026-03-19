package common;

public class ParseResult<T> {
	private boolean success;
	private T value;
	
	public ParseResult(boolean success, T value) {
		this.success = success;
		this.value = value;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public T getValue() {
		return value;
	}
}
