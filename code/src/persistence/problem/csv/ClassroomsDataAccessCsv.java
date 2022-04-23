package persistence.problem.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import business.errorhandler.logic.ErrorHandler;
import business.errorhandler.model.ErrorType;
import business.loghandler.LogHandler;
import business.problem.Classroom;
import persistence.problem.ClassroomsDataAccess;
import persistence.problem.csv.utils.CsvUtils;

public class ClassroomsDataAccessCsv implements ClassroomsDataAccess {

	@Override
	public List<Classroom> loadClassrooms(String filename) {
		List<Classroom> classrooms = new ArrayList<Classroom>();

		List<String> lines = readLines(filename);
		
		if (lines == null)
			return classrooms;

		for (int i = 1; i < lines.size(); i++) // Ignore header
			classrooms.add(lineToClassroom(lines.get(i)));

		return classrooms;
	}

	private List<String> readLines(String filename) {
		List<String> lines = null;
		
		try {

			lines = CsvUtils.readLinesFromCsv(filename);

		} catch (FileNotFoundException e) {

			LogHandler.getInstance().log(Level.SEVERE, ClassroomsDataAccessCsv.class.getName(), "readLines",
					e.getLocalizedMessage(), e);
			ErrorHandler.getInstance().addError(new ErrorType("CLASSROOMS csv file not found.", e));

		} catch (IOException e) {

			LogHandler.getInstance().log(Level.SEVERE, ClassroomsDataAccessCsv.class.getName(), "readLines",
					e.getLocalizedMessage(), e);
			ErrorHandler.getInstance()
					.addError(new ErrorType("Error encountered while loading the CLASSROOMS csv file.", e));

		}
		
		return lines;
	}

	private Classroom lineToClassroom(String line) {
		String[] fields = line.split(";");
		// TODO: Set fields to the classroom
		Classroom c = new Classroom();
		return c;
	}

}
