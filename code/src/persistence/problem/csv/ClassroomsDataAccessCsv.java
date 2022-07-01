package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Classroom;
import business.problem.model.ClassroomType;
import persistence.filemanager.FileManager;
import persistence.problem.ClassroomsDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class ClassroomsDataAccessCsv implements ClassroomsDataAccess {
	public static final String CSVNAME = "CLASSROOMS";

	@Override
	public Map<String, Classroom> loadClassrooms(String filename,
			FileManager fileManager)
			throws InputValidationException, PersistenceException
	{
		Map<String, Classroom> classrooms = new HashMap<String, Classroom>();

		List<String> lines = fileManager.readLinesFromFile(filename);
		for (int i = 1; i < lines.size(); i++) { // Ignore header
			Classroom c = lineToClassroom(lines.get(i), i + 1);
			classrooms.put(c.getCode(), c);
		}
		return classrooms;
	}

	private Classroom lineToClassroom(String line, int lineNumber)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 3, CSVNAME, lineNumber);

		String code = fields[0].trim();
		String type = fields[1].trim();
		String capacity = fields[2].trim();

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

	private void validate(String code, String type, String capacity,
			int lineNumber) throws InputValidationException
	{
		String csvName = CSVNAME;

		// Code validation
		ValidationUtils.validateString(code, csvName, lineNumber);

		// Type validation
		ValidationUtils.validateString(type, csvName, lineNumber);

		String[] values = { "T", "L" };
		ValidationUtils.validateStringValues(type, csvName, values,
				lineNumber);

		// Capacity validation
		ValidationUtils.validateString(capacity, csvName, lineNumber);
		ValidationUtils.validatePositiveInt(capacity, csvName,
				lineNumber);
	}
}
