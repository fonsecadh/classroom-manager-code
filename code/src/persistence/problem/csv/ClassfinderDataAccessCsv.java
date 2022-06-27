package persistence.problem.csv;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import business.classfinder.model.ClassfinderQuery;
import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.ClassroomType;
import persistence.filemanager.FileManager;
import persistence.problem.ClassfinderDataAccess;
import persistence.problem.csv.utils.ValidationUtils;

public class ClassfinderDataAccessCsv implements ClassfinderDataAccess {
	public static final String CSVNAME = "QUERIES";

	@Override
	public List<ClassfinderQuery> loadQueries(String filename,
			FileManager fileManager)
			throws PersistenceException, InputValidationException
	{
		List<ClassfinderQuery> queries = new ArrayList<ClassfinderQuery>();

		List<String> lines = fileManager.readLinesFromFile(filename);

		for (int i = 1; i < lines.size(); i++) // Ignore header
			queries.add(lineToQuery(lines.get(i), i));

		return queries;
	}

	private ClassfinderQuery lineToQuery(String line, int lineNumber)
			throws InputValidationException
	{
		String[] fields = line.split(";", -1); // -1 allows empty
						       // strings to be included
						       // in the array

		ValidationUtils.validateColumns(fields, 8, CSVNAME, lineNumber);

		String startDate = fields[0].trim();
		String endDate = fields[1].trim();
		String startTime = fields[2].trim();
		String endTime = fields[3].trim();
		String duration = fields[4].trim();
		String attendants = fields[5].trim();
		String classroomType = fields[6].trim();
		String nResults = fields[7].trim();

		validate(startDate, endDate, startTime, endTime, duration,
				attendants, classroomType, nResults,
				lineNumber);

		ClassroomType ct = null;
		switch (classroomType) {
		case "T":
			ct = ClassroomType.THEORY;
			break;
		case "L":
			ct = ClassroomType.LABORATORY;
			break;
		}
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

		ClassfinderQuery q = new ClassfinderQuery(sd, ed);
		q.setStartTime(st);
		q.setEndTime(et);
		q.setDurationInHours(Double.parseDouble(duration));
		q.setNumberOfAttendants(Integer.parseInt(attendants));
		q.setClassroomType(ct);
		q.setNumberOfResults(Integer.parseInt(nResults));

		return q;
	}

	private void validate(String startDate, String endDate,
			String startTime, String endTime, String duration,
			String attendants, String classroomType,
			String nResults, int lineNumber)
			throws InputValidationException
	{
		String csvName = CSVNAME;

		// Start date validation
		ValidationUtils.validateString(startDate, csvName, lineNumber);
		ValidationUtils.validateDate(startDate, csvName, lineNumber);

		// End date validation
		ValidationUtils.validateString(endDate, csvName, lineNumber);
		ValidationUtils.validateDate(endDate, csvName, lineNumber);

		// Start time validation
		ValidationUtils.validateString(startTime, csvName, lineNumber);
		ValidationUtils.validateTime(startTime, csvName, lineNumber);

		// End time validation
		ValidationUtils.validateString(endTime, csvName, lineNumber);
		ValidationUtils.validateTime(endTime, csvName, lineNumber);

		// Duration validation
		ValidationUtils.validateString(duration, csvName, lineNumber);
		ValidationUtils.validatePositiveDouble(duration, csvName,
				lineNumber);

		// Attendants validation
		ValidationUtils.validateString(attendants, csvName, lineNumber);
		ValidationUtils.validatePositiveInt(attendants, csvName,
				lineNumber);

		// Classroom type validation
		ValidationUtils.validateString(classroomType, csvName,
				lineNumber);

		String[] valuesType = { "T", "L" };
		ValidationUtils.validateStringValues(classroomType, csvName,
				valuesType, lineNumber);

		// Number of results validation
		ValidationUtils.validateString(nResults, csvName, lineNumber);
		ValidationUtils.validatePositiveInt(nResults, csvName,
				lineNumber);
	}
}
