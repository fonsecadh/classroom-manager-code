package persistence.problem.csv;

import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;
import persistence.problem.AcademicWeeksDataAccess;
import persistence.problem.csv.utils.CsvUtils;
import persistence.problem.csv.utils.ValidationUtils;

public class AcademicWeeksDataAccessCsv implements AcademicWeeksDataAccess {

	public static final String CSVNAME = "WEEKS";

	@Override
	public void loadAcademicWeeks(String filename, Map<String, Group> groups)
			throws PersistenceException, InputValidationException {

		List<String> lines = CsvUtils.readLinesFromCsv(filename, AcademicWeeksDataAccessCsv.class.getName(),
				"loadAcademicWeeks", CSVNAME);

		String header = lines.get(0);
		validateHeader(header);
		String[] headerArray = header.split(";", -1);

		for (int i = 1; i < lines.size(); i++)
			addWeeksToGroup(lines.get(i), i, groups, headerArray);

	}

	private void addWeeksToGroup(String line, int lineNumber, Map<String, Group> groups, String[] header)
			throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		if (fields.length != fields.length) {
			String msg = String.format("Wrong line format in %s csv file: different column size, line %d", CSVNAME,
					lineNumber);
			throw new InputValidationException(msg);
		}

		String groupCode = fields[0].trim();
		ValidationUtils.validateString(groupCode, CSVNAME, lineNumber);

		// Add weeks to group
		Group g = groups.get(groupCode);
		for (int i = 1; i < header.length; i++) {
			String week = header[i];
			if (fields[i].equalsIgnoreCase("S"))
				g.addAcademicWeek(week);
		}

	}

	private void validateHeader(String header) throws InputValidationException {

		String[] fields = header.split(";", -1); // -1 allows empty strings to be included in the array

		for (String f : fields) {
			ValidationUtils.validateString(f, CSVNAME, 0);
		}

	}

}
