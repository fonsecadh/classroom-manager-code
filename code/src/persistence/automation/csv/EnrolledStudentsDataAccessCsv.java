package persistence.automation.csv;

import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Group;
import persistence.filemanager.FileManager;
import persistence.problem.csv.utils.ValidationUtils;

public class EnrolledStudentsDataAccessCsv {
	public static final String CSVNAME = "ENROLLED";

	public void loadEnrolledStudents(String filename,
			Map<String, Group> groups, FileManager fileManager)
			throws PersistenceException, InputValidationException
	{
		List<String> lines = fileManager.readLinesFromFile(filename);

		String header = lines.get(0);
		validateHeader(header);
		String[] headerArray = header.split(";", -1);

		for (int i = 1; i < lines.size(); i++)
			addEnrolledStudentsToGroup(lines.get(i), i + 1, groups,
					headerArray);
	}

	private void addEnrolledStudentsToGroup(String line, int lineNumber,
			Map<String, Group> groups, String[] header)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		String groupType = fields[0].trim();
		String groupName = fields[1].trim();

		validate(fields, groupType, groupName, header, lineNumber);

		// Add enrolled students to groups
		for (int i = 2; i < header.length; i++) {
			String subject = header[i];
			String groupCode = String.format("%s.%s.%s", subject,
					groupType, groupName);
			Group g = groups.get(groupCode);
			if (g == null) {
				String msg = String.format(
						"Non existing code for group in %s csv file (%s), line %d",
						CSVNAME, groupCode, lineNumber);
				throw new InputValidationException(msg);
			}
			String enrolled = fields[i].trim();
			if (enrolled != "") {
				// Validate and parse enrolled students
				validate(enrolled, lineNumber);
				int nStudents = Integer.parseInt(enrolled);
				g.setNumberOfStudents(nStudents);
				// Add group to map
				groups.put(g.getCode(), g);
			}
		}
	}

	private void validate(String enrolled, int lineNumber)
			throws InputValidationException
	{
		// Number of enrolled students validation
		ValidationUtils.validateString(enrolled, CSVNAME, lineNumber);
		ValidationUtils.validatePositiveInt(enrolled, CSVNAME,
				lineNumber);
	}

	private void validate(String[] fields, String groupType,
			String groupName, String[] header, int lineNumber)
			throws InputValidationException
	{
		// Column validation
		ValidationUtils.validateColumns(fields, header.length, CSVNAME,
				lineNumber);

		// Type validation
		ValidationUtils.validateString(groupType, CSVNAME, lineNumber);
		String[] valuesType = { "T", "S", "L" };
		ValidationUtils.validateStringValues(groupType, CSVNAME,
				valuesType, lineNumber);

		// Name validation
		ValidationUtils.validateString(groupName, CSVNAME, lineNumber);
	}

	private void validateHeader(String header)
			throws InputValidationException
	{
		String[] fields = header.split(";", -1); // -1 allows empty
							 // strings to be
							 // included in the
							 // array
		for (String f : fields) {
			ValidationUtils.validateString(f, CSVNAME, 1);
		}
	}
}
