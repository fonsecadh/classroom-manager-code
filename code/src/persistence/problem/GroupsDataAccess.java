package persistence.problem;

import java.util.Map;

import business.errorhandler.exceptions.InputValidationException;
import business.errorhandler.exceptions.PersistenceException;
import business.problem.Group;
import business.problem.Subject;

public interface GroupsDataAccess {

	Map<String, Group> loadGroups(String filename, Map<String, Subject> subjects)
			throws PersistenceException, InputValidationException;

}
