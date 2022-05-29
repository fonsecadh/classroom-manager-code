package business.errorhandler.exceptions;

public class InputValidationException extends Exception {
	private static final long serialVersionUID = 1L;

	public InputValidationException(String message) {
		super(message);
	}
}
