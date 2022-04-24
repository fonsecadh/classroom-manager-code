package persistence.problem.csv.utils;

import java.util.Arrays;

import business.errorhandler.exceptions.InputValidationException;

public class ValidationUtils {

	public static void validateString(String str, String csvName, int lineNumber) throws InputValidationException {

		if (str == null || str.trim().equals("")) {
			String msg = String.format("Null or empty code in %s csv file, line %d", csvName, lineNumber);
			throw new InputValidationException(msg);
		}

	}

	public static void validateStringValues(String str, String csvName, String[] values, int lineNumber)
			throws InputValidationException {

		String expected = "";
		for (int i = 0; i < expected.length(); i++) {
			expected += values[i];
			if (i != expected.length() - 1)
				expected += ", ";
		}

		if (!Arrays.stream(values).anyMatch(str::equals)) {
			String msg = String.format("Wrong type in CLASSROOM csv file (Expected: %s, Received: %s), line %d",
					expected, str, lineNumber);
			throw new InputValidationException(msg);
		}

	}

	public static void validatePositiveInt(String str, String csvName, int lineNumber) throws InputValidationException {

		int number = 0;

		try {
			number = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			String msg = String.format("Wrong capacity in %s csv file (not a number: %s), line %d", csvName, number,
					lineNumber);
			throw new InputValidationException(msg);
		}

		if (number <= 0) {
			String msg = String.format("Wrong capacity in %s csv file (cero or negative: %s), line %d", csvName, number,
					lineNumber);
			throw new InputValidationException(msg);
		}

	}

	public static void validateDate(String str, String csvName, int lineNumber) throws InputValidationException {

		String[] fields = str.trim().split(".", -1);

		if (fields.length != 2) {
			String msg = String.format(
					"Wrong date format in %s csv file (Expected format: HH.MM, Received: %s), line %d", csvName, str,
					lineNumber);
			throw new InputValidationException(msg);
		}

		String hour = fields[0];
		String min = fields[1];

		try {
			Integer.parseInt(hour);
		} catch (NumberFormatException e) {
			String msg = String.format("Wrong date format in %s csv file (hour is not a number: %s), line %d", csvName,
					hour, lineNumber);
			throw new InputValidationException(msg);
		}

		try {
			Integer.parseInt(min);
		} catch (NumberFormatException e) {
			String msg = String.format("Wrong date format in %s csv file (minutes are not a number: %s), line %d",
					csvName, min, lineNumber);
			throw new InputValidationException(msg);
		}

	}

}
