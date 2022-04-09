package business.errorhandler.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import business.errorhandler.model.ErrorType;

public class ErrorHandler {

	private static final ErrorHandler INSTANCE = new ErrorHandler();

	private List<ErrorType> errors;

	private ErrorHandler() {
		this.errors = new ArrayList<ErrorType>();
	}

	public static ErrorHandler getInstance() {
		return INSTANCE;
	}

	public boolean anyErrors() {
		return !errors.isEmpty();
	}

	public void addError(ErrorType error) {
		errors.add(error);
	}

	public List<ErrorType> getErrors() {
		return new ArrayList<ErrorType>(errors);
	}

	public List<String> getCustomErrorMessages() {
		return errors.stream().map(e -> e.getCustomMessage()).collect(Collectors.toList());
	}

	public void clearErrors() {
		errors.clear();
	}

}
