package business.errorhandler.model;

public class ErrorType {

	private String customMsg;
	private Exception e;

	public ErrorType(Exception e) {
		this.customMsg = getMsgForExceptionType(e);
		this.e = e;
	}

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

	private String getMsgForExceptionType(Exception e) {
		return "FATAL ERROR while executing the program. Terminating...";
	}

}
