package persistence.problem.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Subject;
import persistence.filemanager.FileManager;
import persistence.problem.SubjectDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class SubjectDataAccessCsv implements SubjectDataAccess {
	public static final String CSVNAME = "SUBJECTS";

	@Override
	public Map<String, Subject> loadSubjects(String filename,
			FileManager fileManager)
			throws InputValidationException, PersistenceException
	{
		Map<String, Subject> subjects = new HashMap<String, Subject>();

		List<String> lines = fileManager.readLinesFromFile(filename);
		for (int i = 1; i < lines.size(); i++) { // Ignore header
			Subject s = lineToSubject(lines.get(i), i);
			subjects.put(s.getCode(), s);
		}
		return subjects;
	}

	private Subject lineToSubject(String line, int lineNumber)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 3, CSVNAME, lineNumber);

		String code = fields[0].trim();
		String course = fields[1].trim();
		String semester = fields[2].trim();

		validate(code, course, semester, lineNumber);

		Subject s = new Subject();
		s.setCode(code);
		s.setCourse(course);
		s.setSemester(semester);

		return s;
	}

	private void validate(String code, String course, String semester,
			int lineNumber) throws InputValidationException
	{
		String csvName = CSVNAME;

		// Code validation
		ValidationUtils.validateString(code, csvName, lineNumber);

		// Course validation
		ValidationUtils.validateString(course, csvName, lineNumber);

		// Semester validation
		ValidationUtils.validateString(semester, csvName, lineNumber);
	}
}
