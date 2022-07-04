package persistence.automation.csv;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.ClassroomType;
import business.problem.model.Group;
import business.problem.model.GroupLanguage;
import business.problem.model.schedule.Day;
import business.problem.model.schedule.GroupSchedule;
import business.problem.utils.ProblemUtils;
import persistence.filemanager.FileManager;
import persistence.problem.csv.utils.ValidationUtils;

public class PlanningDataAccessCsv {
	public static final String CSVNAME = "PLANNING";

	public Map<String, Group> loadGroupsFromPlanning(String filename,
			FileManager fileManager)
			throws PersistenceException, InputValidationException
	{
		Map<String, Group> groups = new HashMap<String, Group>();

		List<String> lines = fileManager.readLinesFromFile(filename);
		for (int i = 1; i < lines.size(); i++) { // Ignore header
			processLine(lines.get(i), i + 1, groups);
		}
		return groups;
	}

	private void processLine(String line, int lineNumber,
			Map<String, Group> groups)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 7, CSVNAME, lineNumber);

		String groupCode = fields[0].trim();
		String startDate = fields[1].trim();
		String startTime = fields[2].trim();
		String endDate = fields[3].trim();
		String endTime = fields[4].trim();
		// The rest of the columns are ignored

		validate(groupCode, startDate, startTime, endDate, endTime,
				lineNumber);

		String[] stFields = startTime.trim().split("\\.", -1);
		String stHour = stFields[0];
		String stMin = stFields[1];

		String[] etFields = endTime.trim().split("\\.", -1);
		String etHour = etFields[0];
		String etMin = etFields[1];

		LocalTime st = LocalTime.of(Integer.parseInt(stHour),
				Integer.parseInt(stMin));
		LocalTime et = LocalTime.of(Integer.parseInt(etHour),
				Integer.parseInt(etMin));

		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("dd/MM/yyyy");

		LocalDate sd = LocalDate.parse(startDate, formatter);
		LocalDate ed = LocalDate.parse(endDate, formatter);

		Day day = ProblemUtils.getDay(sd);

		// Group
		Group g = groups.get(groupCode);
		if (g == null) {
			g = new Group();
			g.setCode(groupCode);
			ClassroomType ct = ProblemUtils
					.getClassroomTypeFromGroupCode(
							groupCode);
			GroupLanguage gl = ProblemUtils
					.getGroupLanguageFromGroupCode(
							groupCode);
			g.setClassroomType(ct);
			g.setGroupLanguage(gl);
		}
		// Group schedule
		GroupSchedule gs = new GroupSchedule();
		gs.setDay(day);
		gs.setStart(st);
		gs.setFinish(et);
		if (!g.getAllGroupSchedules().contains(gs)) {
			g.addGroupSchedule(gs);
		}
		// Academic weeks
		List<String> weeks = ProblemUtils.getAcademicWeeks(sd, ed);
		for (String w : weeks) {
			if (!g.getAcademicWeeks().contains(w))
				g.addAcademicWeek(w);
		}
		// Add group to map
		groups.put(g.getCode(), g);
	}

	private void validate(String groupCode, String startDate,
			String startTime, String endDate, String endTime,
			int lineNumber) throws InputValidationException
	{
		String csvName = CSVNAME;

		// Group code validation
		ValidationUtils.validateString(groupCode, csvName, lineNumber);

		// Start date validation
		ValidationUtils.validateString(startDate, csvName, lineNumber);
		ValidationUtils.validateDate(startDate, csvName, lineNumber);

		// Start time validation
		ValidationUtils.validateString(startTime, csvName, lineNumber);
		ValidationUtils.validateTime(startTime, csvName, lineNumber);

		// End date validation
		ValidationUtils.validateString(endDate, csvName, lineNumber);
		ValidationUtils.validateDate(endDate, csvName, lineNumber);

		// End time validation
		ValidationUtils.validateString(endTime, csvName, lineNumber);
		ValidationUtils.validateTime(endTime, csvName, lineNumber);
	}
}
