package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.model.Group;
import persistence.filemanager.FileManager;

public interface AcademicWeeksDataAccess {

	void loadAcademicWeeks(String filename, Map<String, Group> groups,
			FileManager fileManager)
			throws PersistenceException, InputValidationException;

}
