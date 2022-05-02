package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.ClassroomType;
import business.problem.Group;
import business.problem.GroupLanguage;
import business.problem.Subject;
import persistence.filemanager.FileManager;
import persistence.problem.GroupsDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class GroupsDataAccessCsv implements GroupsDataAccess {

	public static final String CSVNAME = "GROUPS";

	@Override
	public Map<String, Group> loadGroups(String filename, Map<String, Subject> subjects, FileManager fileManager)
			throws PersistenceException, InputValidationException {

		Map<String, Group> groups = new HashMap<String, Group>();

		List<String> lines = fileManager.readLinesFromFile(filename);

		for (int i = 1; i < lines.size(); i++) { // Ignore header
			Group g = lineToGroup(lines.get(i), i, subjects);
			groups.put(g.getCode(), g);
		}

		return groups;

	}

	private Group lineToGroup(String line, int lineNumber, Map<String, Subject> subjects)
			throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		ValidationUtils.validateColumns(fields, 5, CSVNAME, lineNumber);

		String code = fields[0].trim();
		String subjectCode = fields[1].trim();
		String nStudents = fields[2].trim();
		String type = fields[3].trim();
		String language = fields[4].trim();

		validate(code, subjectCode, nStudents, type, language, lineNumber);

		ClassroomType ct = null;

		switch (type) {
		case "T":
			ct = ClassroomType.THEORY;
			break;
		case "L":
			ct = ClassroomType.LABORATORY;
			break;
		}

		GroupLanguage gl = null;

		switch (language) {
		case "ES":
			gl = GroupLanguage.SPANISH;
			break;
		case "EN":
			gl = GroupLanguage.ENGLISH;
			break;
		}

		Group g = new Group();
		g.setCode(code);
		g.setNumberOfStudents(Integer.parseInt(nStudents));
		g.setClassroomType(ct);
		g.setGroupLanguage(gl);

		Subject s = subjects.get(subjectCode);
		if (s == null) {
			String msg = String.format("Non existing code for subject in %s csv file (%s), line %d", CSVNAME,
					subjectCode, lineNumber);
			throw new InputValidationException(msg);
		}

		s.addGroup(g);
		subjects.put(s.getCode(), s);

		return g;

	}

	private void validate(String code, String subjectCode, String nStudents, String type, String language,
			int lineNumber) throws InputValidationException {

		String csvName = CSVNAME;

		// Code validation
		ValidationUtils.validateString(code, csvName, lineNumber);

		// Subject code validation
		ValidationUtils.validateString(subjectCode, csvName, lineNumber);

		// Number of students validation
		ValidationUtils.validateString(nStudents, csvName, lineNumber);
		ValidationUtils.validatePositiveInt(nStudents, csvName, lineNumber);

		// Type validation
		ValidationUtils.validateString(type, csvName, lineNumber);

		String[] valuesType = { "T", "L" };
		ValidationUtils.validateStringValues(type, csvName, valuesType, lineNumber);

		// Language
		ValidationUtils.validateString(language, csvName, lineNumber);

		String[] valuesLang = { "EN", "ES" };
		ValidationUtils.validateStringValues(language, csvName, valuesLang, lineNumber);

	}

}
