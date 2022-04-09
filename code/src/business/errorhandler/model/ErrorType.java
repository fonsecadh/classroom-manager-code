package business.errorhandler.model;

public class ErrorType {

	private String customMsg;
	private Exception e;

	public ErrorType(String customMsg, Exception e) {
		this.customMsg = customMsg;
		this.e = e;
	}

	public String getCustomMessage() {
		return customMsg;
	}

	public Exception getException() {
		return e;
	}

}
