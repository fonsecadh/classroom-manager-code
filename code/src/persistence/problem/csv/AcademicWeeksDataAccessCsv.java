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

		String header = lines.get(0);
		validateHeader(header);
		String[] headerArray = header.split(";", -1);

		for (int i = 1; i < lines.size(); i++)
			addWeeksToGroup(lines.get(i), i, groups, headerArray);

	}

	private void addWeeksToGroup(String line, int lineNumber,
			Map<String, Group> groups, String[] header)
			throws InputValidationException
	{

		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, header.length, CSVNAME,
				lineNumber);

		String groupCode = fields[0].trim();
		ValidationUtils.validateString(groupCode, CSVNAME, lineNumber);

		// Add weeks to group
		Group g = groups.get(groupCode);
		if (g == null) {
			String msg = String.format(
					"Non existing code for group in %s csv file (%s), line %d",
					CSVNAME, groupCode, lineNumber);
			throw new InputValidationException(msg);
		}

		for (int i = 1; i < header.length; i++) {
			String week = header[i];
			if (fields[i].equalsIgnoreCase("S"))
				g.addAcademicWeek(week);
		}

	}

	private void validateHeader(String header)
			throws InputValidationException
	{

		String[] fields = header.split(";", -1); // -1 allows empty
							 // strings to be
							 // included in the
							 // array

		for (String f : fields) {
			ValidationUtils.validateString(f, CSVNAME, 0);
		}

	}

}
