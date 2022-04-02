package business.errorhandler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {

	private static final ErrorHandler INSTANCE = new ErrorHandler();

	private List<Exception> errors;

	private ErrorHandler() {
		this.errors = new ArrayList<Exception>();
	}

	public static ErrorHandler getInstance() {
		return INSTANCE;
	}

	public boolean anyErrors() {
		return !errors.isEmpty();
	}

	public void addError(Exception error) {
		errors.add(error);
	}

	public void showErrors(PrintStream printStream) {
		errors.forEach(e -> printStream.println(e.toString()));
	}

	public void clearErrors() {
		errors.clear();
	}

}
