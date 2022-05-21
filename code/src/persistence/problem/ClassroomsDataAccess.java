package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Classroom;
import persistence.filemanager.FileManager;

public interface ClassroomsDataAccess {

	Map<String, Classroom> loadClassrooms(String filename, FileManager fileManager)
			throws InputValidationException, PersistenceException;

}
