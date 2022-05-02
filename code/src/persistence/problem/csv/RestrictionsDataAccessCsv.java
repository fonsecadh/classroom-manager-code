package persistence.problem.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.alg.greed.model.Restriction;
import business.alg.greed.model.RestrictionType;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Classroom;
import business.problem.Group;
import persistence.filemanager.FileManager;
import persistence.problem.RestrictionsDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class RestrictionsDataAccessCsv implements RestrictionsDataAccess {

	public static final String CSVNAME = "RESTRICTIONS";

	@Override
	public Map<String, List<Restriction>> loadRestrictions(String filename, Map<String, Classroom> classrooms,
			Map<String, Group> groups, FileManager fileManager) throws PersistenceException, InputValidationException {

		Map<String, List<Restriction>> restrictions = new HashMap<String, List<Restriction>>();

		List<String> lines = fileManager.readLinesFromFile(filename);

		for (int i = 1; i < lines.size(); i++) { // Ignore header
			lineToRestrictions(lines.get(i), i, classrooms, groups, restrictions);
		}

		return restrictions;
	}

	private void lineToRestrictions(String line, int lineNumber, Map<String, Classroom> classrooms,
			Map<String, Group> groups, Map<String, List<Restriction>> restrictions) throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		ValidationUtils.validateColumns(fields, 3, CSVNAME, lineNumber);

		String groupCode = fields[0].trim();
		String restrictionType = fields[1].trim();
		String classroomCode = fields[2].trim();

		validate(groupCode, restrictionType, classroomCode, lineNumber);

		RestrictionType rt = null;

		switch (restrictionType) {
		case "P":
			rt = RestrictionType.POSITIVE;
			break;
		case "N":
			rt = RestrictionType.NEGATIVE;
			break;
		}

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

		Restriction r = new Restriction();
		r.setClassroom(c);
		r.setType(rt);

		List<Restriction> rList = restrictions.get(g.getCode());
		if (rList == null)
			rList = new ArrayList<Restriction>();
		rList.add(r);

		restrictions.put(g.getCode(), new ArrayList<Restriction>(rList));

	}

	private void validate(String groupCode, String restrictionType, String classroomCode, int lineNumber)
			throws InputValidationException {

		String csvName = CSVNAME;

		// Group code validation
		ValidationUtils.validateString(groupCode, csvName, lineNumber);

		// Restriction type validation
		ValidationUtils.validateString(restrictionType, csvName, lineNumber);

		String[] valuesType = { "P", "N" };
		ValidationUtils.validateStringValues(restrictionType, csvName, valuesType, lineNumber);

		// Classroom code validation
		ValidationUtils.validateString(classroomCode, csvName, lineNumber);

	}

}
