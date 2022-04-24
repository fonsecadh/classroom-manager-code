package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;

public interface AcademicWeeksDataAccess {

	void loadAcademicWeeks(String filename, Map<String, Group> groups)
			throws PersistenceException, InputValidationException;

}
