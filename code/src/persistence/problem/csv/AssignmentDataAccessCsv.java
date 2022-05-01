package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.greed.model.Assignment;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;
import business.problem.Group;
import persistence.filemanager.FileManager;
import persistence.problem.AssignmentsDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class AssignmentDataAccessCsv implements AssignmentsDataAccess {

	public static final String CSVNAME = "ASSIGNMENTS";

	@Override
	public Map<String, Assignment> loadGroupSchedule(String filename, Map<String, Group> groups,
			Map<String, Classroom> classrooms, FileManager fileManager)
			throws PersistenceException, InputValidationException {

		Map<String, Assignment> assignments = new HashMap<String, Assignment>();

		List<String> lines = fileManager.readLinesFromFile(filename);

		for (int i = 1; i < lines.size(); i++) { // Ignore header
			Assignment a = lineToAssignment(lines.get(i), i, groups, classrooms);
			assignments.put(a.getGroup().getCode(), a);
		}

		return assignments;

	}

	private Assignment lineToAssignment(String line, int lineNumber, Map<String, Group> groups,
			Map<String, Classroom> classrooms) throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		if (fields.length != 2) {
			String msg = String.format("Wrong line format in %s csv file: different column size, line %d", CSVNAME,
					lineNumber);
			throw new InputValidationException(msg);
		}

		String groupCode = fields[0].trim();
		String classroomCode = fields[1].trim();

		validate(groupCode, classroomCode, lineNumber);

		Group g = groups.get(groupCode);
		if (g == null) {
			String msg = String.format("Non existing code for group in %s csv file (%s), line %d", CSVNAME, groupCode,
					lineNumber);
			throw new InputValidationException(msg);
		}

		Classroom c = classrooms.get(classroomCode);
		if (c == null) {
			String msg = String.format("Non existing code for classroom in %s csv file (%s), line %d", CSVNAME,
					classroomCode, lineNumber);
			throw new InputValidationException(msg);
		}

		Assignment a = new Assignment(groups.get(groupCode));
		a.setClassroom(classrooms.get(classroomCode));

		return a;

	}

	private void validate(String groupCode, String classroomCode, int lineNumber) throws InputValidationException {

		String csvName = CSVNAME;

		// Code validation
		ValidationUtils.validateString(groupCode, csvName, lineNumber);

		// Subject code validation
		ValidationUtils.validateString(classroomCode, csvName, lineNumber);

	}

}
