package persistence.problem.csv;

import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Group;
import persistence.filemanager.FileManager;
import persistence.problem.AcademicWeeksDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class AcademicWeeksDataAccessCsv implements AcademicWeeksDataAccess {
	public static final String CSVNAME = "WEEKS";

	@Override
	public void loadAcademicWeeks(String filename,
			Map<String, Group> groups, FileManager fileManager)
			throws PersistenceException, InputValidationException
	{
		List<String> lines = fileManager.readLinesFromFile(filename);
		for (int i = 1; i < lines.size(); i++) { // Ignore header
			addWeeksToGroup(lines.get(i), i + 1, groups);
		}
	}

	private void addWeeksToGroup(String line, int lineNumber,
			Map<String, Group> groups)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 2, CSVNAME, lineNumber);

		String groupCode = fields[0].trim();
		String weeks = fields[1].trim();

		validate(groupCode, weeks, lineNumber);

		// Add weeks to group
		Group g = groups.get(groupCode);
		if (g == null) {
			String msg = String.format(
					"Non existing code for group in %s csv file (%s), line %d",
					CSVNAME, groupCode, lineNumber);
			throw new InputValidationException(msg);
		}
		String[] wArr = weeks.split(",");
		for (String w : wArr) {
			String week = w.trim();
			if (!week.equals("") && week.startsWith("S")) {
				g.addAcademicWeek(week);
			}
		}
	}

	private void validate(String groupCode, String weeks, int lineNumber)
			throws InputValidationException
	{
		String csvName = CSVNAME;

		// Group code validation
		ValidationUtils.validateString(groupCode, csvName, lineNumber);
	}
}
