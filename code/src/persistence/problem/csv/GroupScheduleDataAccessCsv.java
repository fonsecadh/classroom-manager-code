package persistence.problem.csv;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;
import business.problem.schedule.Day;
import business.problem.schedule.GroupSchedule;
import persistence.filemanager.FileManager;
import persistence.problem.GroupScheduleDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class GroupScheduleDataAccessCsv implements GroupScheduleDataAccess {

	public static final String CSVNAME = "GROUPSCHEDULE";

	@Override
	public List<GroupSchedule> loadGroupSchedule(String filename, Map<String, Group> groups, FileManager fileManager)
			throws PersistenceException, InputValidationException {

		List<GroupSchedule> groupSchedule = new ArrayList<GroupSchedule>();

		List<String> lines = fileManager.readLinesFromFile(filename);

		for (int i = 1; i < lines.size(); i++) // Ignore header
			groupSchedule.add(lineToGroupSchedule(lines.get(i), i, groups));

		return groupSchedule;

	}

	private GroupSchedule lineToGroupSchedule(String line, int lineNumber, Map<String, Group> groups)
			throws InputValidationException {

		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		if (fields.length != 4) {
			String msg = String.format("Wrong line format in %s csv file: different column size, line %d", CSVNAME,
					lineNumber);
			throw new InputValidationException(msg);
		}

		String groupCode = fields[0].trim();
		String day = fields[1].trim();
		String startTime = fields[2].trim();
		String endTime = fields[3].trim();

		validate(groupCode, day, startTime, endTime, lineNumber);

		Day d = null;

		switch (day) {
		case "L":
			d = Day.MONDAY;
			break;
		case "M":
			d = Day.TUESDAY;
			break;
		case "X":
			d = Day.WEDNESDAY;
			break;
		case "J":
			d = Day.THURSDAY;
			break;
		case "V":
			d = Day.FRIDAY;
			break;
		}

		String[] stFields = startTime.trim().split("\\.", -1);
		String stHour = stFields[0];
		String stMin = stFields[1];

		String[] etFields = endTime.trim().split("\\.", -1);
		String etHour = etFields[0];
		String etMin = etFields[1];

		LocalTime st = LocalTime.of(Integer.parseInt(stHour), Integer.parseInt(stMin));
		LocalTime et = LocalTime.of(Integer.parseInt(etHour), Integer.parseInt(etMin));

		GroupSchedule gs = new GroupSchedule();
		gs.setDay(d);
		gs.setStart(st);
		gs.setFinish(et);

		Group g = groups.get(groupCode);
		if (g == null) {
			String msg = String.format("Non existing code for group in %s csv file (%s), line %d", CSVNAME, groupCode,
					lineNumber);
			throw new InputValidationException(msg);
		}

		g.addGroupSchedule(gs);
		groups.put(g.getCode(), g);

		return gs;

	}

	private void validate(String groupCode, String day, String startTime, String endTime, int lineNumber)
			throws InputValidationException {

		String csvName = CSVNAME;

		// Group code validation
		ValidationUtils.validateString(groupCode, csvName, lineNumber);

		// Day validation
		ValidationUtils.validateString(day, csvName, lineNumber);

		String[] values = { "L", "M", "X", "J", "V" };
		ValidationUtils.validateStringValues(day, csvName, values, lineNumber);

		// Start time
		ValidationUtils.validateString(startTime, csvName, lineNumber);
		ValidationUtils.validateTime(startTime, csvName, lineNumber);

		// End time
		ValidationUtils.validateString(endTime, csvName, lineNumber);
		ValidationUtils.validateTime(endTime, csvName, lineNumber);

	}

}
