package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;
import business.problem.ClassroomType;
import persistence.problem.ClassroomsDataAccess;
import persistence.problem.csv.utils.CsvUtils;
import persistence.problem.csv.utils.ValidationUtils;

public class ClassroomsDataAccessCsv implements ClassroomsDataAccess {

	@Override
	public Map<String, Classroom> loadClassrooms(String filename)
			throws InputValidationException, PersistenceException {

		Map<String, Classroom> classrooms = new HashMap<String, Classroom>();

		List<String> lines = CsvUtils.readLinesFromCsv(filename, ClassroomsDataAccessCsv.class.getName(),
				"loadClassrooms", "CLASSROOMS");

		for (int i = 1; i < lines.size(); i++) { // Ignore header
			Classroom c = lineToClassroom(lines.get(i), i);
			classrooms.put(c.getCode(), c);
		}

		return classrooms;

	}

	private Classroom lineToClassroom(String line, int lineNumber) throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		if (fields.length != 3) {
			String msg = String.format("Wrong line format in CLASSROOM csv file: different column size, line %d",
					lineNumber);
			throw new InputValidationException(msg);
		}

		String code = fields[0];
		String type = fields[1];
		String capacity = fields[2];

		validate(code, type, capacity, lineNumber);

		ClassroomType ct = null;

		switch (type) {
		case "T":
			ct = ClassroomType.THEORY;
			break;
		case "L":
			ct = ClassroomType.LABORATORY;
			break;
		}

		Classroom c = new Classroom();
		c.setCode(code);
		c.setType(ct);
		c.setNumberOfSeats(Integer.parseInt(capacity));

		return c;

	}

	private void validate(String code, String type, String capacity, int lineNumber) throws InputValidationException {

		String csvName = "CLASSROOM";

		// Code validation
		ValidationUtils.validateString(code, csvName, lineNumber);

		// Type validation
		ValidationUtils.validateString(type, csvName, lineNumber);

		String[] values = { "T", "L" };
		ValidationUtils.validateStringValues(type, csvName, values, lineNumber);

		// Capacity validation
		ValidationUtils.validateString(capacity, csvName, lineNumber);
		ValidationUtils.validatePositiveInt(capacity, csvName, lineNumber);

	}

}
