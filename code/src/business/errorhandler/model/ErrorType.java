package business.errorhandler.model;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;

/**
 * Wrapper for the errors. This wrapper allows the developers to manage custom
 * error messages for the encountered exceptions while executing the program.
 * 
 * @author Hugo Fonseca Díaz
 *
 */
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

	public String getCustomMessage()
	{
		return customMsg;
	}

	public Exception getException()
	{
		return e;
	}

	/**
	 * Obtains the message for the exception. If the exception has a custom
	 * type, it will return the original message. However, if the exception
	 * is not a custom one, the predefined message for fatal errors is
	 * returned. This allows the developer to show a friendly message on the
	 * console while storing the true error in the LOG file.
	 * 
	 * @param e The exception.
	 * @return The message associated with the exception.
	 */
	private String getMsgForExceptionType(Exception e)
	{
		if (e instanceof InputValidationException)
			return e.getMessage();
		else if (e instanceof PersistenceException)
			return e.getMessage();
		return "FATAL ERROR while executing the program. Terminating...";
	}
}
