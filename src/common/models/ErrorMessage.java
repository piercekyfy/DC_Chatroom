package common.models;


import common.MessageBuilder;


public abstract class ErrorMessage<E extends ErrorMessage<E>> extends Message<E>  {
	protected int code;
	protected int subCode;
	protected int sourceCode;
	
	/**
	 * 
	 * @param code The error code itself, e.g. INVALID_HEADER_ERROR (400).
	 * @param subCode A further specifying sub-code contained in the error's content, e.g. INVALID_OR_MISSING_ARG (1).
	 * @param sourceCode The original request's code, e.g. BROADCAST (1).
	 */
	public ErrorMessage(int code, int subCode, int sourceCode) {
		this.code = code;
		this.subCode = subCode;
		this.sourceCode = sourceCode;
	}
	
	@Override
	public MessageBuilder serialize() {
		return new MessageBuilder()
				.setCode(code)
				.appendContentInt(subCode)
				.appendContentInt(sourceCode);
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getSubCode() {
		return subCode;
	}
	public void setSubCode(int subCode) {
		this.subCode = subCode;
	}
	public int getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(int sourceCode) {
		this.sourceCode = sourceCode;
	}
}