package business.errorhandler.exceptions;

public class ArgumentException extends Exception {
	private static final long serialVersionUID = 1L;

	public ArgumentException(String message) {
		super(message);
	}
}
