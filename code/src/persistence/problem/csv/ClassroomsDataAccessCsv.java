package persistence.problem.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.loghandler.LogHandler;
import business.problem.Classroom;
import business.problem.ClassroomType;
import persistence.problem.ClassroomsDataAccess;
import persistence.problem.csv.utils.CsvUtils;

public class ClassroomsDataAccessCsv implements ClassroomsDataAccess {

	@Override
	public List<Classroom> loadClassrooms(String filename) throws InputValidationException, PersistenceException {
		List<Classroom> classrooms = new ArrayList<Classroom>();

		List<String> lines = readLines(filename);

		if (lines == null)
			return classrooms;

		for (int i = 1; i < lines.size(); i++) // Ignore header
			classrooms.add(lineToClassroom(lines.get(i), i));

		return classrooms;
	}

	private Classroom lineToClassroom(String line, int lineNumber) throws InputValidationException {
		String[] fields = line.split(";", -1); // -1 allows empty strings to be included in the array

		if (fields.length != 3) {
			String msg = String.format("Wrong line format in CLASSROOM csv file: different column size, line %d",
					lineNumber);
			throw new InputValidationException(msg);
		}

		String code = fields[0];
		String type = fields[1];
		String capacity = fields[2];

		validate(code, type, capacity, lineNumber);

		ClassroomType ct = null;

		switch (type) {
		case "T":
			ct = ClassroomType.THEORY;
			break;
		case "L":
			ct = ClassroomType.LABORATORY;
			break;
		}

		int capacityInt = Integer.parseInt(capacity);

		Classroom c = new Classroom();

		c.setCode(code);
		c.setType(ct);
		c.setNumberOfSeats(capacityInt);

		return c;
	}

	private void validate(String code, String type, String capacity, int lineNumber) throws InputValidationException {

		if (code == null || code.trim().equals("")) {
			String msg = String.format("Wrong code in CLASSROOM csv file, line %d", lineNumber);
			throw new InputValidationException(msg);
		}

		if (type == null || type.trim().equals("") || !(type.equals("T") || type.equals("L"))) {
			String msg = String.format("Wrong type in CLASSROOM csv file, line %d", lineNumber);
			throw new InputValidationException(msg);
		}

		if (capacity == null || capacity.trim().equals("")) {
			String msg = String.format("Wrong capacity in CLASSROOM csv file, line %d", lineNumber);
			throw new InputValidationException(msg);
		}

		int capacityInt = 0;

		try {
			capacityInt = Integer.parseInt(capacity);
		} catch (NumberFormatException e) {
			String msg = String.format("Wrong capacity in CLASSROOM csv file, line %d", lineNumber);
			throw new InputValidationException(msg);
		}

		if (capacityInt <= 0) {
			String msg = String.format("Wrong capacity in CLASSROOM csv file, line %d", lineNumber);
			throw new InputValidationException(msg);
		}

	}

	private List<String> readLines(String filename) throws PersistenceException {
		List<String> lines = null;

		try {

			lines = CsvUtils.readLinesFromCsv(filename);

		} catch (FileNotFoundException e) {

			LogHandler.getInstance().log(Level.SEVERE, ClassroomsDataAccessCsv.class.getName(), "readLines",
					e.getLocalizedMessage(), e);

			throw new PersistenceException("CLASSROOM csv file not found");

		} catch (IOException e) {

			LogHandler.getInstance().log(Level.SEVERE, ClassroomsDataAccessCsv.class.getName(), "readLines",
					e.getLocalizedMessage(), e);

			throw new PersistenceException("Error encountered while loading the CLASSROOMS csv file.");

		}

		return lines;
	}

}
